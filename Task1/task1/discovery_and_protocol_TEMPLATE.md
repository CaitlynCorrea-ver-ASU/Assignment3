# Task 1.2: Mystery Service Discovery and Protocol Documentation

**Caitlyn Correa:**
**How I tested:** Extended Client

---

## Part 1: Discovery Log

Document at least 8 test attempts showing your systematic investigation.

### Attempt 1
**Request Sent:**
```json
{
    "type":"analyzer"
}
```

**Response Received:**
```json
{
    "ok":false,
    "message":"Field 'action' does not exist in request. Hint: what action do you want to perform?"
}
```

**What I Learned:**
We learned that the mystery service expects us to perform some type of action and has the client select action type they wish to use (we also are also aware that the request type is 'analyzer')

---

### Attempt 2
**Request Sent:**
```json
{
    "type":"analyzer",
    "action":"wave"
}
```

**Response Received:**
```json
{
    "ok":false,
    "message":"Field 'text' does not exist in request"
}
```

**What I Learned:**
I discovered that another required service request field is text, and that is needed after the action field. Need to learn what actions are available and what text is expected.

---

### Attempt 3
**Request Sent:**
```json
{
    "action":"wave",
    "text":"hello",
    "type":"analyzer"
}
```

**Response Received:**
```json
{
    "ok":false,
    "message":"Action 'wave' not supported. Valid actions: wordcount, charcount, search"
}
```

**What I Learned:**
I have learned that the expected action types are wordcount, charcount, search. Through these action types we can assume that text just needs to be any valid string.

---

### Attempt 4
**Request Sent:**
```json
{
    "action":"wordcount",
    "text":"Hello there my name is Caitlyn Correa",
    "type":"analyzer"
}
```

**Response Received:**
```json
{
    "count":7,
    "action":"wordcount",
    "type":"analyzer",
    "ok":true

    org.json.JSONException: JSONObject["result"] not found.
        at org.json.JSONObject.get(JSONObject.java:520)
        at org.json.JSONObject.getInt(JSONObject.java:694)
        at SockClient.main(SockClient.java:234)
}
```

**What I Learned:**
Currently the client is unable to read our response, we need to adjust our field name - change result to count (at least with wordcount) as is expected.

---

---

### Attempt 5
**Request Sent:**
```json
{
    "action":"wordcount",
    "text":"hello world",
    "type":"analyzer"
}
```

**Response Received:**
```json
{
    "count":2,
    "action":"wordcount",
    "type":"analyzer",
    "ok":true
}
```

**What I Learned:**
We ensured that count was indeed the expected field by adjusting the field name else branch to include count before the result field. We can confirm that word-count does require their to be count and returns back the amount of words in text.

---

### Attempt 6
**Request Sent:**
```json
{
    "action":"charcount",
    "text":"hello world",
    "type":"analyzer"
}
```

**Response Received:**
```json
{
    "count":11,
    "action":
    "charcount",
    "type":"analyzer",
    "ok":true
}
```

**What I Learned:**
We confirmed that charcount is a supported action and that the service returns the character count in the field count. The input "hello world" produced a count of 11, meaning the space between the words is included in the count.

---

### Attempt 7
**Request Sent:**
```json
{
    "action":"search",
    "text":"hello world",
    "type":"analyzer"
}
```

**Response Received:**
```json
{
    "ok":false,
    "message":"Field 'find' does not exist in request"
}
```

**What I Learned:**
Unlike the other two actions, search requires an additional field named find in the request. The service has an error since this field is currently missing - we need to add this field in as well

---

### Attempt 8
**Request Sent:**
```json
{
    "find":"name",
    "action":"search",
    "text":"hello there my name is Caitlyn Correa",
    "type":"analyzer"
}
```

**Response Received:**
```json
{
    "found":true,
    "find":"name",
    "count":1,
    "action":"search",
    "positions":[15],
    "type":"analyzer",
    "ok":true
}
```

**What I Learned:**
We confrimed that search requires the field find (which is a string). The response also includes count, the position, and true/false if the item is found. We are not yet sure if this action is able to handle nonexistent words or duplicates.

### Attempt 9
**Request Sent:**
```json
{
   "find":"name",
   "action":"search",
   "text":"hello there my name is Caitlyn and my last name is Correa so my full name is Caitlyn Correa",
   "type":"analyzer"
}
```

**Response Received:**
```json
{
    "found":true,
    "find":"name",
    "count":3,
    "action":"search",
    "positions":[15,43,69],
    "type":"analyzer",
    "ok":true
}
```

**What I Learned:**
We confrimed that search ius able to handle duplicates (responding with the positions of each word in an array format) and the count response field does indeed count the amount of times an occured word is found and the found field still returns true (as expected) when multiple occurences appear

---

### Attempt 10
**Request Sent:**
```json
{
   "find":"ASU",
   "action":"search",
   "text":"hello there my name is Caitlyn Correa",
   "type":"analyzer"
}
```

**Response Received:**
```json
{
    "found":false,
    "find":"ASU",
    "count":0,
    "action":"search",
    "positions":[],
    "type":"analyzer",
    "ok":true
}
```

**What I Learned:**
We confrimed that search is able to handle a request when the word is not within the text - found becomes false, count is 0, position is an empty array. 

---
## Part 2: Complete Protocol Specification

### Analyzer

The Analyzer service supports three operations: wordcount, charcount, and search AND takes in two different string fields text (used in all 3 operations) and find (used only with search).

#### Word Count 

wordcount: counts the words supplied in text field

**Request:**
```json
{
    "type": "analyzer", -- request type
    "action": "wordcount", -- the action to be performed
    "text": "<String>" -- the text that will have its number of words counted
}
```

**Success Response:**
```json
{
    "count": <int>, -- number of words counted from text
    "action": "wordcount", -- the action to be performed
    "type": "analyzer", -- request type
    "ok": true
}
```

#### Character Count

charcount - counts the number of characters with the text field (spaces included)

**Request:**
```json
{

    "type": "analyzer", -- request type
    "action": "charcount", -- the action to be performed
    "text": "<String>" -- the text whose characters will be counted
}
```

**Success Response:**
```json
{
    "count": <int>, -- number of characters counted, including spaces
    "action": "charcount", -- the action to be performed
    "type": "analyzer", -- request type
    "ok": true
}
```

#### Search

search - searches for a specified string given in the find field (provides wether or not it was found, occurrence count, and postion within an int array)

**Request:**
```json
{
    "type": "analyzer", -- request type
    "action": "search", -- the action to be performed
    "text": "<String>", -- the text to search within
    "find": "<String>" -- the text to search for
}
```

**Success Responses:**

Match Found
```json
{
    "found": true, -- whether the search term was found
    "find": "<String>", -- the search term
    "count": <int>, -- number of matches found
    "action": "search", -- the action to be performed
    "positions": [<int>], -- positions of the matches in the text
    "type": "analyzer", -- request type
    "ok": true
}
```

No Match Found
```json
{
    "found": false,
    "find": "<String>",
    "count": 0,
    "action": "search",
    "positions": [],
    "type": "analyzer",
    "ok": true
}
```

**Error Responses:**

Error for missing action field:
```json
{
     "ok": false,
     "message": "Field 'action' does not exist in request. Hint: what action do you want to perform?"
}
```

Error for missing text field:
```json
{
     "ok": false,
     "message": "Field 'text' does not exist in request"
}
```
Error for missing find field:
```json
{
  "ok": false,
  "message": "Field 'find' does not exist in request"
}
```

Error for unsupported action requested
```json
{
  "ok": false,
  "message": "Action 'wave' not supported. Valid actions: wordcount, charcount, search"
}
```

---

[Document ALL operations you discovered]

---

## Part 3: Summary

**Total Operations Discovered:**
3 (wordcount, charcount, search)

**How I approached discovery:**
I started by adding in the needed sections to client so the mystery service could be an action users could select (starting off by having the client only request the type:analyzer) I analyzed each error message and attempted to add in each piece that would attempt to solve the error message issue. Once I discovered a functionality I would go through and test to see how it worked, and what it supported. I utilized the extension of the Client class to make testing a little bit more straigtforward .

**Most challenging part:**
The most challenging part was figuring out the protocol without access to the mystery service's source code. I had to use the service's error messages to determine what fields were required. Another challenge was that the client was not set up to handle testing the code via trial and error so that also had to be implemented for my style of testing. 