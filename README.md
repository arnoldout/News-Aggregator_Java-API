# NwsAggregatorMiddleLayer
A Java/Spring RESTful API, to a personalised news service. The System also doubles as a web crawler, and will scrape news stories on 
an hourly basis for users to consume. 

The endpoints the user can interact with the API are as follows:

A deployed version of the system can be found at https://nws-mongo-api.herokuapp.com, to access the endpoints, pre-append the following
endpoints with that URL.

An example endpoint would be:
```
  /getArticles/:id
```
where id relates to a user id of a profile stored in the MongoDB

An example endpoint would be:
```
  /getArticles/:id
```
where id relates to a user id of a profile stored in the MongoDB

An example endpoint would be:
```
  /addLike/:id/:like
```
where id is a user id associated to a user
and a like is a string, such as **russia** or **parliament**

The like will be added to the account, opening up more news stories to the user

An example endpoint would be:
```
  /allLikes
```
This request will return all the news topics that are available in the database
As the database gets altered every hour, these are subject to change

An example endpoint would be:
```
  /getProfile/:profileId
```
Where profileId is the user id associated with an account, this returns all of the data associated with the user account, except the password

An example endpoint would be:

```
  /login
```
This is a post request, where the body text will store the user login credentials
I.e, the username and password, The user's id will be returned upon success


An example endpoint would be:

```
  /addProfile
```
This method creates a new account if the username is unique.
This is a post request, where the body text will store the user login credentials
I.e, the username and password, The user's id will be returned upon success


