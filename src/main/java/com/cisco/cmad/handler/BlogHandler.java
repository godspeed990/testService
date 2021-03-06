package com.cisco.cmad.handler;



import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import com.cisco.cmad.model.*;
import com.cisco.cmad.Service.BlogService;
import java.util.Base64;


import io.vertx.core.eventbus.EventBus;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import com.google.inject.Inject;

public class BlogHandler {
	

	  Logger logger = LoggerFactory.getLogger(BlogHandler.class);
	    @Inject BlogService blogService;
	    
		private EventBus eventBus;

public void getBlogs(RoutingContext rc) {
   String queryParam = rc.request().getParam("tag");
   if (logger.isDebugEnabled()) {
      logger.debug("Tag search ? , tag :" + queryParam);
   }
   String authorization = rc.request().getHeader("Authorization"); 
   System.out.println(authorization);
   String userName = Base64.getDecoder().decode(authorization.substring(authorization.lastIndexOf(" ")+1,authorization.indexOf(":"))).toString();
   String password = Base64.getDecoder().decode(authorization.substring(authorization.indexOf(":")+1)).toString();
   rc.vertx().executeBlocking(future -> {
   try {
	   List<BlogEntry> blogList;
      if (queryParam != null && queryParam.trim().length() > 0) {
       blogList = blogService.getBlogs(Optional.ofNullable(queryParam));	            
      } else {
                //get all blogs
        blogList = blogService.getBlogs(Optional.empty());
      }
      future.complete(Json.encodePrettily(blogList));
   } catch (Exception ex) {
       logger.error("Exception while trying to fetch blogs " + queryParam, ex);
       future.fail(ex);
   }
}, res -> {           
   if (res.succeeded()) {
	   JsonArray val = (JsonArray) res.result();
   rc.response().setStatusCode(200);
   rc.response().end(val.encode());
   }
   else {
	   rc.response().setStatusCode(400);
	   rc.response().end();
   }
   

    }
    ); 
}
public void setEventBus(EventBus eb){
   eventBus =eb;
}

public JsonArray getUserInfo(List<BlogEntry> blogs){
	
	return new JsonArray();
}
public void storeBlog(RoutingContext rc) {
  String jSonString = rc.getBodyAsString(); //get JSON body as String
  String authorization = rc.request().getHeader("Authorization");
  //JsonObject jSonString = rc.getBodyAsJson();
  String userName = Base64.getDecoder().decode(authorization.substring(authorization.lastIndexOf(" ")+1,authorization.indexOf(":"))).toString();
  String password = Base64.getDecoder().decode(authorization.substring(authorization.indexOf(":")+1)).toString();

  if (logger.isDebugEnabled())
     logger.debug("JSON String from POST " + jSonString);

  BlogEntry blog = Json.decodeValue(jSonString, BlogEntry.class);
/*BlogEntry blog =new BlogEntry(Optional.empty(),rc.getBodyAsJson().getValue("content").toString(),
		rc.getBodyAsJson().getValue("title").toString(),rc.getBodyAsJson().getValue("tags").toString(),
				Optional.empty(),Optional.empty()
		);*/
  blog.setUserName(userName);
  blogService.storeBlog(blog);
  if (logger.isDebugEnabled())
     logger.debug("RegistrationDTO object after json Decode : " + blog);
  rc.response().setStatusCode(201).end();
try{	        
  eventBus.send("com.cisco.cmad.user.authenticate",new JsonObject().put("userName",userName).put("password",password),response->{
	  if (response.succeeded()){
		  logger.debug("user:"+userName+"authenticated");
		  JsonObject resp = (JsonObject) response.result().body();
		  if (resp.getString("userId")!=null){
			  blog.setUserName(userName);
			  blogService.storeBlog(blog);
              if (logger.isDebugEnabled())
                  logger.debug("Blog stored successfully");
              rc.response().setStatusCode(201).end();
		  }
			else {
				rc.response().setStatusCode(400).end();
			}
	  }
	  else {
	  }
  });
  }
catch (Exception e){		  
	logger.error("Error occurred while trying to save blog with " + blog.getId(), e);
	rc.response().setStatusCode(400).end();
}
	        
	        
	        //   BlogEntry blogE = new BlogEntry(Optional.ofNullable(null),rc.getBodyAsJson().getValue("content").toString(),rc.getBodyAsJson().getValue("title").toString(),rc.getBodyAsJson().getValue("tags").toString(),userid,Optional.ofNullable(null));
 }

	    
public void submitComment(RoutingContext rc) {
  String jSonString = rc.getBodyAsString(); 
  String blogId = rc.request().getParam("blogId");

  if (logger.isDebugEnabled())
    logger.debug("JSON String from POST " + jSonString + " Blog Id :" + blogId);
    Comment comment = Json.decodeValue(jSonString, Comment.class);
    String authorization = rc.request().getHeader("Authorization");
    
    String userName = Base64.getDecoder().decode(authorization.substring(authorization.lastIndexOf(" ")+1,authorization.indexOf(":"))).toString();
    String password = Base64.getDecoder().decode(authorization.substring(authorization.indexOf(":")+1)).toString();
//	Comment comment = new Comment(rc.getBodyAsJson().getValue("content").toString(),new ObjectId());
    comment.setUserName("Memme");
	blogService.updateBlogWithComments(blogId, comment);
	rc.response().setStatusCode(201).end();
  if (logger.isDebugEnabled())
    logger.debug("Comment object : " + comment);
  	eventBus.send("com.cisco.cmad.user.authenticate",new JsonObject().put("userName",userName).put("password",password),response->{
		if (response.succeeded()){
			  logger.debug("user:"+userName+"authenticated");
			JsonObject resp = (JsonObject) response.result().body();
			if (resp.getString("userId")!=null){
				comment.setUserName(userName);
				
				blogService.updateBlogWithComments(blogId, comment);
				rc.response().setStatusCode(200).end();
				if (logger.isDebugEnabled())
                logger.debug("Comment updated in blog successfully");
				}
			else {
				rc.response().setStatusCode(400).end();
			}
		}
		else {
			rc.response().setStatusCode(400).end();
		}
  		
  	});     
	   
	   }
	   

	        }
