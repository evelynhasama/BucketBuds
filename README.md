Original App Design Project - README
===

# Bucket Buds

## Demo
[Video Demo](https://drive.google.com/file/d/1ZK8MZ1j1714qJx3ydC1XxNhWdI66gihm/view?usp=sharing)

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
Collaborative bucket list app that allows users to create shared bucket lists with their friends and family. Users can schedule bucket list activities and gain inspiration for local and random activities. It helps users plan and reach their fun life goals side by side! 

### App Evaluation
[Evaluation of your app across the following attributes]
- **Category:** Productivity, Lifestyle
- **Mobile:** Android only experience, uses camera, photo gallery, and location
- **Story:** Allows users to create bucket lists with friends and family
- **Market:** everyone, people with friends or a thirst for new experiences
- **Habit:** Once users create a list, they will try to finish all of the items.
- **Scope:** Focused primarily on collaborative bucket lists, event scheduling, and receiving inspiration

## Product Spec

### 1. User Stories (Required and Optional)

**Required Must-have Stories**

* [x] User can sign up with a new user profile
* [x] User can log in/log out of the app
* [x] User can add friends (other users) by username
* [x] User can remove friends
* [x] User can view their profile, bio, list of friends
* [x] User can edit their profile information
* [x] User can create lists shared amongst friends with list descriptions and image
* [x] User can view all of their bucket lists
* [x] User can add activities with details (name, description, location, date/time) to a bucket list 
* [x] User can delete activities in the bucket list
* [x] User can schedule events with the group using the Calendar Provider
* [x] User can filter bucket lists by completed/active status and sort by alphabetical order/last modified/created at
* [x] User can get inspiration for activities they may be interested in through the APIs

**Optional Nice-to-have Stories**

* [ ] User can check if everyone's calenders are free or busy using the [Freebusy: query](https://developers.google.com/calendar/api/v3/reference/freebusy/query)
* [ ] User can upload photos of completed activities into the bucket
    * [ ] User can view photo details and which activity
* [ ] Users can chat with the group through on-device messaging or in-app Parse chat
* [ ] When adding friends, user's contacts can provide suggestions
* [x] User can copy/add over inspiration activities to their lists
* [x] User can add friends to a bucket list after list creation (through Bucket Requests)
* [x] User can modify activity details
* [x] User can route to event locations through on-device Google Maps
* [ ] User can search through their home stream of bucket lists
* [ ] User can search through bucket list for activity
* [x] User can view friends' profiles
* [x] User can edit bucket list details
* [x] User can filter local inspiration activities by mile radius
* [x] User can select a location for the activity through Google Places Autocomplete


### 2. Screen Archetypes

* Login screen
   * User can log in/log out of the app
* Registration screen
   * User can sign up with a new user profile
* Profile screen
    * User can view their profile, bio, list of friends
    * User can remove friends
    * User can edit their profile information
    * User can add friends (other users) by username
* Friends Profile screen
   * User can view friends' profiles
   * User can add friends to a bucket list after list creation (through Bucket Requests)
* Home Stream screen
    * User can view all of their bucket lists
    * User can filter bucket lists by completed/active status and sort by alphabetical order/last modified/created at
* Bucket Requests Screen
   * User can add friends to a bucket list after list creation (through Bucket Requests)
* Create Bucket List screen
    * User can create bucket lists shared amongst friends with list descriptions and image
* Bucket List Activities screen
    * User can add activities with details (name, description, location) to a bucket list
    * User can select a location for the activity through Google Places Autocomplete
    * User can edit bucket list details
* Activity Details screen
    * User can delete activities in the bucket list
    * User can schedule events with the group using the the Calendar Provider
    * User can modify activity details
    * User can route to event locations through on-device Google Maps
* Inspiration screen
    * User can get inspiration for activities they may be interested in through some of the following APIs:
      * [Bored API](https://www.boredapi.com/)
      * [Ticketmaster Dsicovery API](https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/)
      * [SeatGeek API](https://platform.seatgeek.com/#events)
      * [Musement API (actvities search)](https://api-docs.musement.com/docs/activities)
    * User can filter local inspiration activities by mile radius
    * User can copy/add over inspiration activities to their lists

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
   * Login screen (when tap menu item)
* Profile screen
    * Friends Profile screen (when tapping friend)
    * Login screen (after logging out)
* Friends Profile screen
    * None 
* Home Stream screen
    * Bucket List Activities screen (when tapping on bucket list)
    * Create Bucket List Screen (when tap menu item)
    * Bucket Requests Screen (when tap menu item)
* Bucket Requests Screen
    * None 
* Create Bucket List screen
    * Home screen (after creating or back)
* Bucket List Activities screen
    * Activity details screen (when tapping on activity)
    * Create activity dialog (when tap menu item)
       * Google Places Autocomplete (when editing location)
* Activity Details screen
    * Google Places Autocomplete (when editing location)
    * Map to location (when tap menu item)
    * Calendar to schedule event (when tap menu item)
* Inspiration screen
    * Inspo Activity Details Screen
* Inspo Activity Details Screen
    * None

## Wireframes
* Login and Create Account Workflow
   * <img src="https://github.com/evelynhasama/BucketBuds/blob/main/wireframes/login.jpg" width=500>
* Main Workflow
   * <img src="https://github.com/evelynhasama/BucketBuds/blob/main/wireframes/mainworkflow.jpg" width=800>

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
   | bio           | String          | words to describe themself |
   | image         | File            | profile picture |
   | userPub       | Pointer<UserPub>| points to user's public user |
 
#### UserPub

   | Property      | Type            | Description |
   | ------------- | --------------- | ------------|
   | objectId      | String          | unique id for the public user (default field) |
   | user          | Pointer(User)   | user who's information is contained in the object |
   | bucketCount   | Int             | user's number of buckets |
   | buckets       | Relation(BucketList) | relation to user's buckets |
   | friendCount   | Int             | user's number of friends |
   | friends       | Relation(User)  | relation to user's friends |
   | email         | String          | user's email address|
  
#### BucketList

   | Property      | Type            | Description |
   | ------------- | --------------- | ------------|
   | objectId      | String          | unique id for the bucket (default field) |
   | name          | String          | bucket list name |
   | image         | File            | bucket list cover image |
   | description   | String          | description of bucket list |
   | completed     | Boolean         | false (active), true (completed) |
   | createdAt     | DateTime        | date when bucket is created (default field) |
   | updatedAt     | DateTime        | date when bucket is last updated (default field) |
   | users         | Relation(User)  | relation to users who own the bucket list |
   | activities    | Relation(Activity)| relation to activities in the bucket |
   | userCount     | Number          | number of users who own the bucket |
   | activityCount | Number          | number of activities in the bucket |
   | bucketRequests| Relation(BucketRequest) | relation to users who requested the bucket |
   
#### Activity

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the activity (default field) |
   | name          | String   | activity name |
   | description   | String   | description of activity |
   | completed     | Boolean  | false (active), true (completed) |
   | location      | String   | location name |
   | allDay        | Boolean  | true if the event is an all day event | 
   | startDate     | Date     | event Start Date | 
   | endDate       | Date     | event End Date |
   | bucket        | Pointer  | points to bucket list |
   | web           | String   | link to the activity web page |
   | eventCreated  | Boolean  | true when calender is event created |
   | address       | String   | address provided by Google Places autocomplete |
 
 #### BucketRequest

   | Property      | Type     | Description |
   | ------------- | -------- | ------------|
   | objectId      | String   | unique id for the activity (default field) |
   | fromUser      | Pointer  | points to user who sent the request |
   | bucket        | Pointer  | points to the bucket the user wants to join |
   | status        | String   | pending, approved, denied |
   | receiver      | Pointer  | bucket user who approved/denied the request | 
 

### Networking
#### List of network requests to Parse by screen
* Login screen
    * logInInBackground()
* Registration screen
    * signUpInBackground()
* Profile screen
    * PUT update user profile info 
    * GET get user's profile info
    * PUT add/remove friends
    * GET get user's friends
    * GET search for users by username
* Friends Profile screen
    * GET get friend's profile info
    * GET get friend's buckets
    * GET get user's buckets
    * GET get bucket requests
    * PUT create bucket requests
* Home Stream screen
    * GET query for the user's own bucket lists
    * deleteInBackground()
* Bucket Requests Screen
    * GET get user's outgoing requests
    * GET get user's bucket lists
    * GET get incoming requests for the user's buckets
    * PUT respond to bucket requests
* Create Bucket List screen
    * PUT create a bucket list
* Bucket List Activities screen
    * GET query for activities
    * PUT create new activities
    * PUT update bucket list activity count, completed status
* Activity Details screen
    * PUT modify the activity info
    * deleteInBackground()
* Inspiration screen
    * None
* Inspo Activity Details Screen
    * PUT add activity to bucket list

#### API Endpoints
* [Bored API](https://www.boredapi.com/documentation)
   * Get a random event: GET http://www.boredapi.com/api/activity/
* [Ticketmaster Dsicovery API](https://developer.ticketmaster.com/products-and-docs/apis/discovery-api/v2/)
* [SeatGeek API](https://platform.seatgeek.com/#events)
* [Musement API (actvities search)](https://api-docs.musement.com/docs/activities)
* [Google Places Autocomplete](https://developers.google.com/maps/documentation/places/android-sdk/autocomplete#option_2_use_an_intent_to_launch_the_autocomplete_activity)
