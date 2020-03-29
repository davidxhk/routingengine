# Tests for `change_support_request_type`

## Contents
The following types of tests for `change_support_request_type` are provided in this test suite.
  1. Valid inputs
  2. Errors for uuid input
  3. Errors for type input
  4. Miscellaneous cases

## Test Suite
| ID    | Title                      | Input                                                                                                          |
| :---: | -------------------------- | -------------------------------------------------------------------------------------------------------------- |
| 1.1   | Valid uuid and type index  | `change_support_request_type {uuid:"<--valid support request uuid string-->", type:1}`                         |
| 1.1   | Valid uuid and type string | `change_support_request_type {uuid:"<--valid support request uuid string-->", type:"GENERAL_ENQUIRY"}`         |
| 2.1   | Missing uuid               | `change_support_request_type {type:1}`                                                                         |
| 2.2.1 | Invalid uuid case 1        | `change_support_request_type {uuid:[], type:1}`                                                                |
| 2.2.2 | Invalid uuid case 2        | `change_support_request_type {uuid:{}, type:1}`                                                                |
| 2.2.3 | Invalid uuid case 3        | `change_support_request_type {uuid:"hahaha test test", type:1}`                                                |
| 3.1   | Missing type               | `change_support_request_type {uuid:"<--valid support request uuid string-->"}`                                 |
| 3.2.1 | Invalid type case 1        | `change_support_request_type {uuid:"<--valid support request uuid string-->", type:[]}`                        |
| 3.2.2 | Invalid type case 2        | `change_support_request_type {uuid:"<--valid support request uuid string-->", type:{}}`                        |
| 3.3   | Invalid type case 3        | `change_support_request_type {uuid:"<--valid support request uuid string-->", type:999}`                       |
| 3.4   | Invalid type case 4        | `change_support_request_type {uuid:"<--valid support request uuid string-->", type:"blabla"}`                  |
| 4.1   | Missing input              | `change_support_request_type {}`                                                                               |
| 4.2   | Unexpected arguments       | `change_support_request_type {uuid:"<--valid support request uuid string-->", type:1, something:"something?"}` |
| 4.3.1 | Malformed arguments case 1 | `change_support_request_type "testtest"`                                                                       |
| 4.3.2 | Malformed arguments case 2 | `change_support_request_type []`                                                                               |
| 4.3.3 | Malformed arguments case 3 | `change_support_request_type ;!/`                                                                              |
| 4.3.4 | Malformed arguments case 4 | `change_support_request_type }}}}`                                                                             |

## Expected Output
| ID    | Title                      | Result  | Payload                               | Comments                                                     |
| :---: | -------------------------- | :-----: | ------------------------------------- | ------------------------------------------------------------ |
| 1.1   | Valid uuid and type index  | success | `{<--support request json object-->}` |                                                              |
| 1.1   | Valid uuid and type string | success | `{<--support request json object-->}` |                                                              |
| 2.1   | Missing uuid               | failure | `"uuid missing"`                      |                                                              |
| 2.2.1 | Invalid uuid case 1        | failure | `"uuid invalid"`                      |                                                              |
| 2.2.2 | Invalid uuid case 2        | failure | `"uuid invalid"`                      |                                                              |
| 2.2.3 | Invalid uuid case 3        | failure | `"uuid invalid"`                      |                                                              |
| 3.1   | Missing type               | failure | `"type missing"`                      |                                                              |
| 3.2.1 | Invalid type case 1        | failure | `"type invalid"`                      |                                                              |
| 3.2.2 | Invalid type case 2        | failure | `"type invalid"`                      |                                                              |
| 3.3   | Invalid type case 3        | failure | `"type index out of bounds"`          |                                                              |
| 3.4   | Invalid type case 4        | failure | `"type string invalid"`               |                                                              |
| 4.1   | Missing input              | failure | `"uuid missing"`                      |                                                              |
| 4.2   | Unexpected arguments       | success | `{<--support request json object-->}` | Unexpected arguments should be ignored silently              |
| 4.3.1 | Malformed arguments case 1 | failure | `"malformed arguments"`               |                                                              |
| 4.3.2 | Malformed arguments case 2 | failure | `"malformed arguments"`               |                                                              |
| 4.3.3 | Malformed arguments case 3 | failure | `"malformed arguments"`               |                                                              |
| 4.3.4 | Malformed arguments case 4 | failure | `"malformed arguments"`               |                                                              |
