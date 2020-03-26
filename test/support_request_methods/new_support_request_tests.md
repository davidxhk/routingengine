# Tests for `new_support_request`

## Contents
The following types of tests for `new_support_request` are provided in this test suite.
  1. Valid inputs
  2. Errors for name input
  3. Errors for email input
  4. Errors for type input
  5. Errors for address input
  6. Miscellaneous cases

## Test Suite
| ID    | Title                          | Input                                                                                                              |
| :---: | ------------------------------ | ------------------------------------------------------------------------------------------------------------------ |
| 1.1   | Name, email, and type index    | `new_support_request {name:"bob", email:"bob@abc.com", type:1}`                                                    |
| 1.2   | Name, email, and type string   | `new_support_request {name:"bob", email:"bob@abc.com", type:"GENERAL_ENQUIRY"}`                                    |
| 1.3   | Name, email, type, and address | `new_support_request {name:"bob", email:"bob@abc.com", type:1, address:127.0.0.1}`                                 |
| 2.1   | Missing name                   | `new_support_request {email:"bob@abc.com", type:1}`                                                                |
| 2.2.1 | Invalid name case 1            | `new_support_request {name:[], email:"bob@abc.com", type:1}`                                                       |
| 2.2.2 | Invalid name case 2            | `new_support_request {name:{}, email:"bob@abc.com", type:1}`                                                       |
| 3.1   | Missing email                  | `new_support_request {name:"bob", type:1}`                                                                         |
| 3.2.1 | Invalid email case 1           | `new_support_request {name:"bob", email:[], type:1}`                                                               |
| 3.2.2 | Invalid email case 2           | `new_support_request {name:"bob", email:{}, type:1}`                                                               |
| 4.1   | Missing type                   | `new_support_request {name:"bob", email:"bob@abc.com"}`                                                            |
| 4.2.1 | Invalid type case 1            | `new_support_request {name:"bob", email:"bob@abc.com", type:[]}`                                                   |
| 4.2.2 | Invalid type case 2            | `new_support_request {name:"bob", email:"bob@abc.com", type:{}}`                                                   |
| 4.3   | Invalid type case 3            | `new_support_request {name:"bob", email:"bob@abc.com", type:999}`                                                  |
| 4.4   | Invalid type case 4            | `new_support_request {name:"bob", email:"bob@abc.com", type:"blabla"}`                                             |
| 5.1.1 | Invalid address case 1         | `new_support_request {name:"bob", email:"bob@abc.com", type:1, address:[]}`                                        |
| 5.1.2 | Invalid address case 2         | `new_support_request {name:"bob", email:"bob@abc.com", type:1, address={}}`                                        |
| 5.1.3 | Invalid address case 3         | `new_support_request {name:"bob", email:"bob@abc.com", type:1, address="adsflasdfmlsdf"}`                          |
| 5.1.4 | Invalid address case 4         | `new_support_request {name:"bob", email:"bob@abc.com", type:1, address="999.999.999.999"}`                         |
| 6.1   | Missing input                  | `new_support_request {}`                                                                                           |
| 6.2   | Unexpected arguments           | `new_support_request {name:"bob", email:"bob@abc.com", type:1, something:"something?"}`                            |
| 6.3.1 | Malformed arguments case 1     | `new_support_request "testtest"`                                                                                   |
| 6.3.2 | Malformed arguments case 2     | `new_support_request []`                                                                                           |
| 6.3.3 | Malformed arguments case 3     | `new_support_request ;!/`                                                                                          |
| 6.3.4 | Malformed arguments case 4     | `new_support_request }}}}`                                                                                         |

## Expected Output
| ID    | Title                          | Result  | Payload                               | Comments                                                         |
| :---: | ------------------------------ | :-----: | ------------------------------------- | ---------------------------------------------------------------- |
| 1.1   | Name, email, and type index    | success | `{<--support request json object-->}` |                                                                  |
| 1.2   | Name, email, and type string   | success | `{<--support request json object-->}` |                                                                  |
| 1.3   | Name, email, type, and address | success | `{<--support request json object-->}` | Address field is optional, defaults to the socket's host address |
| 2.1   | Missing name                   | failure | `"name missing"`                      |                                                                  |
| 2.2.1 | Invalid name case 1            | failure | `"name invalid"`                      |                                                                  |
| 2.2.2 | Invalid name case 2            | failure | `"name invalid"`                      |                                                                  |
| 3.1   | Missing email                  | failure | `"email missing"`                     |                                                                  |
| 3.2.1 | Invalid email case 1           | failure | `"email invalid"`                     |                                                                  |
| 3.2.2 | Invalid email case 2           | failure | `"email invalid"`                     |                                                                  |
| 4.1   | Missing type                   | failure | `"type missing"`                      |                                                                  |
| 4.2.1 | Invalid type case 1            | failure | `"type invalid"`                      |                                                                  |
| 4.2.2 | Invalid type case 2            | failure | `"type invalid"`                      |                                                                  |
| 4.3   | Invalid type case 3            | failure | `"type index out of bounds"`          |                                                                  |
| 4.4   | Invalid type case 4            | failure | `"type string invalid"`               |                                                                  |
| 5.1.1 | Invalid address case 1         | failure | `"address invalid"`                   |                                                                  |
| 5.1.2 | Invalid address case 2         | failure | `"address invalid"`                   |                                                                  |
| 5.1.3 | Invalid address case 3         | failure | `"address invalid"`                   |                                                                  |
| 5.1.4 | Invalid address case 4         | failure | `"address invalid"`                   |                                                                  |
| 6.1   | Missing input                  | failure | `"name missing"`                      |                                                                  |
| 6.2   | Unexpected arguments           | success | `{<--support request json object-->}` | Unexpected arguments should be ignored silently                  |
| 6.3.1 | Malformed arguments case 1     | failure | `"malformed arguments"`               |                                                                  |
| 6.3.2 | Malformed arguments case 2     | failure | `"malformed arguments"`               |                                                                  |
| 6.3.3 | Malformed arguments case 3     | failure | `"malformed arguments"`               |                                                                  |
| 6.3.4 | Malformed arguments case 4     | failure | `"malformed arguments"`               |                                                                  |
