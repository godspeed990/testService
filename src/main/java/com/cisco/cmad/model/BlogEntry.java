package com.cisco.cmad.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@Entity("blogs")
@Indexes({
    @Index(fields = @Field("tags")),
    @Index(fields = @Field("userName"))
})
public class BlogEntry {

		@Id
		private ObjectId id;
	    private String content;
		private String title;
		private String tags;
//		private BlogUsers user;
		private Date date ;
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss z");
		@Embedded
		private ArrayList<Comment> comment= new ArrayList<Comment>();
		private String userName;
		private String firstName;
		private String lastName;

		@PrePersist void prePersist() {date= new Date();}

	    public String getId() {
	        return (id != null) ? id.toHexString() : "";
	    }


		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getTags() {
			return tags;
		}
		public void setTags(String tags) {
			this.tags = tags;
		}


		public ArrayList<Comment> getComment() {
			return comment;
		}
		public void addComment(Comment comment) {
			this.comment.add(comment);
		}
		public void addComment(ArrayList<Comment> comment) {
			this.comment.addAll(comment);
		}
		public Date getDate() {
			return date;
		}

}
