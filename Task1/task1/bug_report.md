# StringConcatenation Debugging Exercise

## Overview
The stringconcatenation service is implemented in both client and server, but has **4 bugs** that prevent it from working correctly according to the protocol specification.

The Correct Protocol is in the README.md

---

## The 4 Bugs

---

### Bug #1:  Incorrect Request Field 
**Location:** `SockClient.java`, line 78

**The Problem:**
```
The server was expecting to recieve "string1" as the first field but was instead recieving "str1". The server was rejecting "str1" since it was not the required/expected field name.
Original Code: 
    json.put("str1", str1);
```

**The Fix:**
```
Changed the field name from "str1" to "string1".
Updated Code: 
    json.put("string1", str1);
```

**Why it matters:** 
The client and server need to utilize the same field name in order to properly communicate between one another. Sending the wrong field name prevents the two from processing the request.

**How did you find this:**
I tested the String Concatenation, and noticed the error message stated "Field string1 does not exist in request". 

---

### Bug #2:  Wrong Response Type
**Location:** `SockServer.java`, line 184

**The Problem:**
```
The server returned "concat" as the response type instead of "stringconcatenation" - causing an unkown service type response message
Original Code: 
    res.put("type", "concat");
```

**The Fix:**
```
Changed the response type to "stringconcatenation" to match the service name used in client
Updated Code: 
    res.put("type", "stringconcatenation");
```

**Why it matters:** 
The client uses the response type to determine how to handle the server's response - if they do not match then the client cannot recognize the response 

**How did you find this:**
I tested the String Concatenation, and noticed that the type was showing as concat and not stringconcatenation and there was an Unkown Service message appearing as well. I searched the files using ctrl + f to find instances of concat

---

### Bug #3:  No Validation of String 2
**Location:** `SockServer.java`, line 177-181

**The Problem:**
```
Currently the server is only checking that string 1 exists and is skipping a check for string 2. If string 2 field was missing we risk the server having an execution error
Original Code:
    JSONObject res = testField(req, "string1");
    if (!res.getBoolean("ok")) {
      return res;
    }

```

**The Fix:**
```
Added in a similar check for string 2 as string 1 to ensure it is validated before we attempt our operation (reused string 1's validation check for consistency)
Updated Code:
    JSONObject res = testField(req, "string1");
    if (!res.getBoolean("ok")) {
      return res;
    }
    res = testField(req, "string2");
    if (!res.getBoolean("ok")) {
      return res;
    }
```

**Why it matters:** 
This ensures we are safely checking both strings to allow the server to handle a missing field rather than causing an error that could crash the server

**How did you find this:**
While reviewing concat during the investigation of Bug #2 I noticed that there was a safety check for string 1, but none for string 2. I tested to see if there would be an error from removing string 2 in the client fields, and sure enough the server went down.

---

### Bug #4:  Incorrect Reponse Field
**Location:** `SockServer.java`, line 192 + 'SockClient.java', line 131

**The Problem:**
```
The server is responding with the "combined" field rather than utilizing "result". This violates the convention set within the documentation.
```

**The Fix:**
```
Change the "combined" field to "result" within both Server and Client to help ensure they are consistent with the given documentation (could also potentially have changed the documentation to match the code - but due to README specifiying to leave documentation as it unless specified changing code is the more reasonable choice)
```

**Why it matters:** 
There should be consistency amongst naming conventions (especially in documentation made to help understand how code works) - and by double checking consistency we can ensure no errors occur do to mismatch requests and responses

**How did you find this:**
After reviewing the documentation within the README, I noticed that the desired naming convention was result and not combined.

---