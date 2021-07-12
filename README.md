Original App Design Project - README
===

# Bucket Buds

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Collaborative bucket list app that allows users to create shared bucket lists with their friends and family. Users will schedule their bucket list activities and get inspiration and resources for activities.

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Productivity, Lifestyle
- **Mobile:** Android only experience
- **Story:** Allows users to create bucket lists with friends and family
- **Market:** everyone, people with friends or a thirst for new experiences
- **Habit:** Once users create a list, they will try to finish all of the items.
- **Scope:** Focused primarily on collaborative bucket lists, event scheduling, and receiving inspiration

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* User can sign up with a new user profile
* User can log in/log out of the app
* User can add friends (other users) by phone number or username
* User can remove friends
* User can view their profile, bio, list of friends
* User can edit their profile information
* User can create lists shared amongst friends with list descriptions and ideal finish date
* User can view all of their bucket lists
* User can add activities with details (name, description, location, date/time) to a bucket list 
* User can reorder or delete items in the bucket list
* User can schedule events with the group using the [G Calendar API](https://developers.google.com/calendar/api/v3/reference) and/or [G Calendar Service](https://developers.google.com/apps-script/reference/calendar)
* User can see sort/filter list by completed/active bucket status, alphabetical order
* User can get inspiration for activities they may be interested in through the APIs
* User can find helpful resources on the web
 

**Optional Nice-to-have Stories**

* User can check if everyone's calenders are free or busy using the [Freebusy: query](https://developers.google.com/calendar/api/v3/reference/freebusy/query)
* User can upload photos of completed activities into the bucket
    * User can view photo details and which activity
* Users can chat with the group through on-device messaging or in-app Parse chat
* When adding friends, user's contacts can provide suggestions
* User can copy/add over inspiration activities to their lists
* User can add friends to a bucket list after list creation
* User can modify activity details
* User can route to event locations through on-device Google Maps
* User can search through their home stream of bucket lists
* User can search through bucket list for activity
* User can view friends' profiles


### 2. Screen Archetypes

* Login screen
   * User can log in/log out of the app
* Registration screen
   * User can sign up with a new user profile
* Profile screen
    * User can view their profile, bio, list of friends
    * User can remove friends
    * User can edit their profile information
* Add friends screen (Fragment)
    * User can add friends (other users) by phone number or username
* Home Stream screen
    * User can view all of their bucket lists
* Create Bucket List screen
    * User can create bucket lists shared amongst friends with list descriptions and ideal finish date
* Bucket List screen
    * User can reorder or delete items in the bucket list
    * User can add activities with details (name, description, location) to a bucket list
* Activity Details screen
    * User can schedule events with the group using the [G Calendar API](https://developers.google.com/calendar/api/v3/reference) and/or [G Calendar Service](https://developers.google.com/apps-script/reference/calendar)
* Inspiration screen
    * User can get inspiration for activities they may be interested in through some of the following APIs:
      * [Bored API](https://www.boredapi.com/)
      * [Ticketmaster Dsicovery API](https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/)
      * [SeatGeek API](https://platform.seatgeek.com/#events)
      * [Musement API (actvities search)](https://api-docs.musement.com/docs/activities)
    * User can find helpful resources on the web

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Profile tab
* Home tab
* Inspiration tab

**Flow Navigation** (Screen to Screen)

* Login screen
   * Home Stream screen (after logging in)
   * Registration screen
* Registration screen
   * Home Stream screen (after registering)
   * Login screen
* Profile screen
    * Add Friends screen
* Add Friends screen (Fragment)
    * Profile screen
* Home Stream screen
    * Bucket List screen (when tapping on bucket list)
* Create Bucket List screen
    * Home screen (after creating or back)
* Bucket List screen
    * Home Stream screen (back)
    * Activity details screen (when tapping on activity)
* Activity Details screen
    * Bucket List screen (back)
* Inspiration screen
    * None

## Wireframes
* Login and Create Account Workflow
   * <img src="https://github.com/evelynhasama/BucketBuds/blob/main/wireframes/login.jpg" width=500>
* Main Workflow
   * <img src="https://github.com/evelynhasama/BucketBuds/blob/main/wireframes/mainworkflow.jpg" width=800>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
### Models
   
#### User

   | Property      | Type            | Description |
   | ------------- | --------------- | ------------|
   | objectId      | String          | unique id for the user (default field) |
   | username      | String          | username |
   | password      | String          | password |
   | firstName     | String          | first name |
   | lastName      | String          | last name |
   | email         | String          | gmail address |
   | calenderId    | String          | calenderId for Google Calendar API |
   | bio           | String          | words to describe themself |
   | image         | File            | profile picture |
   | bucketCount   | Number          | number of user's buckets |
   | friendCount   | Number          | number of user's friends |
   | friends       | Relation<User>  | relation to user's friends |
  
#### BucketList

   | Property      | Type            | Description |
   | ------------- | --------------- | ------------|
   | objectId      | String          | unique id for the user post (default field) |
   | name          | String          | bucket list name |
   | image         | File            | bucket list cover image |
   | description   | String          | description of bucket list |
   | completed     | Boolean         | false (active), true (completed) |
   | createdAt     | DateTime        | date when bucket is created (default field) |
   | updatedAt     | DateTime        | date when bucket is last updated (default field) |
   | users         | Relation<User>  | relation to users who own the bucket list |
   
#### Activity

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the activity (default field) |
   | name          | String   | activity name |
   | description   | String   | description of activity |
   | completed     | Boolean  | false (active), true (completed) |
   | location      | String   | location name |
   | time          | DateTime | when event is occuring |
   | eventId       | String   | eventId for Google Calender API |
   | bucket        | Pointer  | points to bucket |
   | emails        | Array    | list of user's gmails |
   | web           | String   | link to the activity web page |


### Networking
#### List of network requests by screen
* Login screen
    * logInInBackground()
       * ```java
         ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    // Failed to login
                    return;
                }
                // User is signed in
            }
         });
         ```
* Registration screen
    * signUpInBackground()
      * ```java
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    // Failed to sign up
                    return;
                }
                // User is signed up
            }
        });
        ```
* Profile screen
    * PUT update user profile info 
    * GET get user's profile info
* Add Friends screen (Fragment)
    * GET query for users by search
      *  ```java
         // querying User class
         query.startsWith("username", text);
         ```
    * PUT update friends list
      * ```java
         friendsRelation = User.relation('friends');
         friendsRelation.add(friend)
         ``` 
* Home Stream screen
    * GET query for the user's own bucket lists
      *  ```java
         // querying BucketList class
         query.whereEqualTo('users', user);
         ``` 
* Create Bucket List screen
    * PUT create a bucket list
      * ```java
         bucketsRelation = BucketList.relation('users');
         bucketsRelation.add(bucketlist)
        ``` 
* Bucket List screen
    * GET query for activities
      *  ```java
         // querying Activity class
         query.whereEqualTo("bucket", bucketlist);
         ``` 
    * PUT create new activities
      * ```java
        Activity.set('bucket', bucket);
        ``` 
* Activity Details screen
    * PUT modify the activity info
* Inspiration screen
    * None

#### OPTIONAL: API Endpoints
* [Google Calendar API](https://developers.google.com/calendar/api/v3/reference) ([Java Docs](https://googleapis.dev/java/google-api-services-calendar/latest/index.html)):
   * Create event: POST https://www.googleapis.com/calendar/v3/calendars/calendarId/events
   * Delete event: DELETE https://www.googleapis.com/calendar/v3/calendars/calendarId/events/eventId
   * Update event: PUT https://www.googleapis.com/calendar/v3/calendars/calendarId/events/eventId
* [Bored API](https://www.boredapi.com/documentation)
   * Get a random event: GET http://www.boredapi.com/api/activity/
* [Ticketmaster Dsicovery API](https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/)
* [SeatGeek API](https://platform.seatgeek.com/#events)
* [Musement API (actvities search)](https://api-docs.musement.com/docs/activities)
