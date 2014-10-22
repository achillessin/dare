Parse.Cloud.afterSave("Comment", function(request) {
  query = new Parse.Query("Challenge");
  query.get(request.object.get("Challenge").id, {
    success: function(challenge) {
      challenge.increment("numComments");
      challenge.save();
    },
    error: function(error) {
      console.error(error.code + " : " + error.message);
    }
  });
});

Parse.Cloud.afterSave("Like", function(request) {
  query = new Parse.Query("Challenge");
  query.get(request.object.get("Challenge").id, {
    success: function(challenge) {
      challenge.increment("numLikes");
      challenge.save();
    },
    error: function(error) {
      console.error(error.code + " : " + error.message);
    }
  });
});

Parse.Cloud.afterDelete("Comment", function(request) {
  query = new Parse.Query("Challenge");
  query.get(request.object.get("Challenge").id, {
    success: function(challenge) {
      challenge.increment("numComments", -1);
      challenge.save();
    },
    error: function(error) {
      console.error(error.code + " : " + error.message);
    }
  });
});

Parse.Cloud.afterDelete("Like", function(request) {
  query = new Parse.Query("Challenge");
  query.get(request.object.get("Challenge").id, {
    success: function(challenge) {
      challenge.increment("numLikes", -1);
      challenge.save();
    },
    error: function(error) {
      console.error(error.code + " : " + error.message);
    }
  });
});
