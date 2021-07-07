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
* User can create lists shared amongst friends with list descriptions and ideal finish date
* User can view all of their bucket lists
* User can add activities with details (name, description, location, date/time) to a bucket list 
* User can reorder or delete items in the bucket list
* User can schedule events with the group using the [G Calendar API](https://developers.google.com/calendar/api/v3/reference) and/or [G Calendar Service](https://developers.google.com/apps-script/reference/calendar)
* Users can route to event locations through on-device Google Maps
* User can see sort/filter list by completed/active bucket status, alphabetical order
* User can get inspiration for activities they may be interested in through the [Eventbrite API](https://www.eventbrite.com/platform/docs/introduction) or [Bored API](https://www.boredapi.com/)
* User can find helpful resources on the web
 

**Optional Nice-to-have Stories**

* User can check if everyone's calenders are free or busy using the [Freebusy: query](https://developers.google.com/calendar/api/v3/reference/freebusy/query)
* User can upload photos of completed activities into the bucket
    * User can view photo details and which activity
* Users can chat with the group through on-device messaging
* When adding friends, user's contacts can provide suggestions
* User can copy/add over inspiration activities to their lists
* User can add friends to a list after list creation
* User can modify activity details
* User can search through their home stream of bucket lists
* User can search through bucket list for activity
* User can sort/filter their home stream of bucket lists

### 2. Screen Archetypes

* Login screen
   * User can log in/log out of the app
* Registration screen
   * User can sign up with a new user profile
* Profile screen
    * User can view their profile, bio, list of friends
    * User can remove friends
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
    * Users can route to event locations through on-device Google Maps
* Inspiration screen
    * User can get inspiration for activities they may be interested in through the [Eventbrite API](https://www.eventbrite.com/platform/docs/introduction) or [Bored API](https://www.boredapi.com/)
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
   * <img src="https://github.com/evelynhasama/BucketBuds/blob/main/wireframes/mainworkflow.jpg" width=900>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
