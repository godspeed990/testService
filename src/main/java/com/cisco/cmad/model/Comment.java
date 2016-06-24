package com.cisco.cmad.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

@Entity("comment")
@Indexes({
    @Index(fields = @Field("userName"))
})
public class Comment {

		@Id
		private ObjectId id;
		private String content;
//		private BlogUsers user;
		private Date date ;
		private String type;
		DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' hh:mm:ss zs");
		@Reference("blogs")
		private ArrayList<Comment> comment= new ArrayList<Comment>();
		private String userName ;
		private String firstName ;
		private String lastName ; 

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

		@PrePersist void prePersist() {date= new Date();}

	    public String getId() {
	        return (id != null) ? id.toHexString() : "";
	    }

	    public void setId(String id) {
	        if (id != null)
	            this.id = new ObjectId(id);
	    }

		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
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
