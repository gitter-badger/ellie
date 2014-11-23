/*
 * Copyright (C) 2014 Michael Pardo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nhaarman.ellie.test;

import android.content.ContentProvider;

import com.nhaarman.ellie.Ellie;
import com.nhaarman.ellie.Model;
import com.nhaarman.ellie.query.Delete;
import com.nhaarman.ellie.query.Insert;
import com.nhaarman.ellie.query.Query;
import com.nhaarman.ellie.query.Select;
import com.nhaarman.ellie.query.Update;
import com.nhaarman.ellie.test.content.EllieSampleProvider;
import com.nhaarman.ellie.test.model.Note;
import com.nhaarman.ellie.test.model.NoteTag;
import com.nhaarman.ellie.test.model.Tag;
import com.nhaarman.ellie.test.shadows.PersistentShadowSQLiteOpenHelper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;
import org.robolectric.shadows.ShadowLog;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import rx.functions.Action1;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = PersistentShadowSQLiteOpenHelper.class)
public class EllieTest {

    private static final int NOTE_COUNT = 100;

    private static final int TAG_COUNT = 10;

    @BeforeClass
    public static void setup() {
        new File("path").delete();
    }

    @Before
    public void initialize() {
        ContentProvider contentProvider = new EllieSampleProvider();
        contentProvider.onCreate();

        ShadowLog.stream = System.out;
        ShadowContentResolver.registerProvider("com.example.ollie", contentProvider);

        Ellie.getInstance().init(Robolectric.application, "OllieSample.db", 1, Ellie.LogLevel.FULL);
    }

    @Test
    public void testPopulateDatabase() {
        final Tag[] tags = new Tag[TAG_COUNT];
        final Random rand = new Random();

        for (int i = 0; i < TAG_COUNT; i++) {
            Tag tag = new Tag();
            tag.name = "TAG " + i;
            tag.save();

            tags[i] = tag;
        }

        for (int i = 0; i < NOTE_COUNT; i++) {
            Note note = new Note();
            note.title = "NOTE " + i;
            note.body = "This is the body for note #" + i;
            note.date = new Date();
            note.save();

            final int tagCount = rand.nextInt(TAG_COUNT);
            final List<Tag> tagList = new ArrayList<Tag>(Arrays.asList(tags));
            Collections.shuffle(tagList);

            for (int j = 0; j < tagCount; j++) {
                NoteTag noteTag = new NoteTag();
                noteTag.note = note;
                noteTag.tag = tagList.remove(0);
                noteTag.save();
            }
        }
    }

    @Test
    public void testSaveEntity() {
        Note note = new Note();
        assertThat(note.id).isNull();

        note.title = "Test note";
        note.body = "Testing saving a note.";
        note.save();
        assertThat(note.id).isNotNull();
        assertThat(note.id).isGreaterThan(0l);
    }

    @Test
    public void testLoadEntity() {
        Note note = new Select().from(Note.class).where(Model.COLUMN_ID + "=?", 1L).fetchSingle();
        assertThat(note).isNotNull();
        assertThat(note.id).isNotNull();
        assertThat(note.id).isGreaterThan(0l);
    }

    @Test
    public void testDeleteEntity() {
        Note note = new Select().from(Note.class).where(Model.COLUMN_ID + "=?", 1L).fetchSingle();
        assertThat(note).isNotNull();
        assertThat(note.id).isNotNull();
        assertThat(note.id).isGreaterThan(0l);

        note.delete();
        assertThat(note).isNotNull();
        assertThat(note.id).isNull();
    }

    @Test
    public void testSelectSql() {
        String sql;
        Query query;

        sql = "SELECT * FROM notes";
        query = new Select().from(Note.class);
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(null);

        sql = "SELECT * FROM notes WHERE _id=?";
        query = new Select().from(Note.class).where(Model.COLUMN_ID + "=?", "1");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(new String[]{"1"});

        sql = "SELECT * FROM notes ORDER BY title ASC";
        query = new Select().from(Note.class).orderBy("title ASC");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(null);

        sql = "SELECT * FROM notes LIMIT 1";
        query = new Select().from(Note.class).limit("1");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(null);

        sql = "SELECT * FROM notes LIMIT 1 OFFSET 10";
        query = new Select().from(Note.class).limit("1").offset("10");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(null);

        sql = "SELECT * FROM notes INNER JOIN noteTags ON notes.id=noteTags.note INNER JOIN tags ON tag.id=noteTags" +
                ".tag WHERE tag.name=? ORDER BY notes.title ASC LIMIT 10 OFFSET 10";
        query = new Select()
                .from(Note.class)
                .innerJoin(NoteTag.class).on("notes.id=noteTags.note")
                .innerJoin(Tag.class).on("tag.id=noteTags.tag")
                .where("tag.name=?", "test")
                .orderBy("notes.title ASC")
                .limit("10")
                .offset("10");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(new String[]{"test"});
    }

    @Test
    public void testInsertSql() {
        String sql;
        Query query;

        sql = "INSERT INTO notes VALUES(?, ?, ?)";
        query = new Insert().into(Note.class).values("Testing INSERT", "Testing INSERT body.", "0");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT", "Testing INSERT body.", "0"});

        sql = "INSERT INTO notes(title) VALUES(?)";
        query = new Insert().into(Note.class, "title").values("Testing INSERT");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT"});

        sql = "INSERT INTO notes(title, body) VALUES(?, ?)";
        query = new Insert().into(Note.class, "title", "body").values("Testing INSERT", "Testing INSERT body.");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT", "Testing INSERT body."});

        sql = "INSERT INTO notes(title, body, date) VALUES(?, ?, ?)";
        query = new Insert().into(Note.class, "title", "body", "date").values(
                "Testing INSERT",
                "Testing INSERT body.", "0"
        );
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(new String[]{"Testing INSERT", "Testing INSERT body.", "0"});

        try {
            new Insert().into(Note.class, "title", "body", "date").values(
                    "Testing INSERT",
                    "Testing INSERT body."
            ).execute();
            assert false;
        } catch (Query.MalformedQueryException e) {
            // Successfully threw exception
            assert true;
        }

    }

    @Test
    public void testUpdateSql() {
        String sql;
        Query query;

        sql = "UPDATE notes SET title='Testing UPDATE'";
        query = new Update(Note.class).set("title='Testing UPDATE'");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(null);

        sql = "UPDATE notes SET title=?";
        query = new Update(Note.class).set("title=?", "Testing UPDATE");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(new String[]{"Testing UPDATE"});

        sql = "UPDATE notes SET title='Testing UPDATE' WHERE _id=1";
        query = new Update(Note.class).set("title='Testing UPDATE'").where(Model.COLUMN_ID + "=1");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(null);

        sql = "UPDATE notes SET title=? WHERE _id=?";
        query = new Update(Note.class).set("title=?", "Testing UPDATE").where(Model.COLUMN_ID + "=?", "1");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(new String[]{"Testing UPDATE", "1"});
    }

    @Test
    public void testDeleteSql() {
        String sql;
        Query query;

        sql = "DELETE FROM notes";
        query = new Delete().from(Note.class);
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(null);

        sql = "DELETE FROM notes WHERE _id=1";
        query = new Delete().from(Note.class).where(Model.COLUMN_ID + "=1");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(null);

        sql = "DELETE FROM notes WHERE _id=?";
        query = new Delete().from(Note.class).where(Model.COLUMN_ID + "=?", "1");
        assertThat(query.getSql()).isEqualTo(sql);
        assertThat(query.getArgs()).isEqualTo(new String[]{"1"});
    }

    @Test
    public void testSelectEntity() {
        // Single note
        Note note = new Select().from(Note.class).fetchSingle();
        assertThat(note).isNotNull();
        assertThat(note.id).isNotNull();
        assertThat(note.id).isGreaterThan(0l);

        // Single note async
        new Select().from(Note.class).observableSingle()
                    .subscribe(
                            new Action1<Model>() {
                                @Override
                                public void call(Model note) {
                                    System.out.println("2");
                                    assertThat(note).isNotNull();
                                    assertThat(note.id).isNotNull();
                                    assertThat(note.id).isGreaterThan(0l);
                                }
                            }
                    );


        // Single note by id
        note = new Select().from(Note.class).where(Note.COLUMN_ID + "=?", 1).fetchSingle();
        assertThat(note).isNotNull();
        assertThat(note.id).isNotNull();
        assertThat(note.id).isGreaterThan(0l);

        // Single tag
        Tag tag = new Select().from(Tag.class).fetchSingle();
        assertThat(tag).isNotNull();
        assertThat(tag.id).isNotNull();
        assertThat(tag.id).isGreaterThan(0l);

        // Save note tag to get guaranteed join result
        NoteTag noteTag = new NoteTag();
        noteTag.note = note;
        noteTag.tag = tag;
        noteTag.save();

        // Many
        List<Note> notes = new Select().from(Note.class).fetch();
        assertThat(notes).isNotNull();
        assertThat(notes.size()).isGreaterThan(0);

        // Join
        notes = new Select("notes.*")
                .from(Note.class)
                .innerJoin(NoteTag.class).on("notes._id=noteTags.note")
                .innerJoin(Tag.class).on("tags._id=noteTags.tag")
                .where("tags._id=?", tag.id.toString())
                .fetch();
        assertThat(notes).isNotNull();
        assertThat(notes.size()).isGreaterThan(0);
    }

    @Test
    public void testFetchValue() {
        long sum = new Select("SUM(date)").from(Note.class).fetchValue(long.class);
        assertThat(sum).isGreaterThan(0);

        int count = new Select("COUNT(*)").from(Note.class).fetchValue(int.class);
        assertThat(count).isGreaterThan(0);
    }

    @Test
    public void testSaveNoteTagWithoutTag() {
        NoteTag noteTag = new NoteTag();
        noteTag.save();
        assertThat(noteTag.id).isGreaterThan(0l);
    }

    @Test
    public void testDeleteByQuery() {
        Note note = new Note();
        note.body = "this is draft";
        note.save();
        new Delete().from(Note.class).where(Note.COLUMN_ID + "=?", note.id.toString()).execute();

        // TODO: This seems like a bit of work
        // assertThat(note.id).isNull();
    }
}