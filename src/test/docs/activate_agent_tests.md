# Tests for `activate_agent`

## Contents
The following types of tests for `activate_agent` are provided in this test suite.
  1. Valid inputs
  2. Errors for uuid input
  3. Errors for activate input
  4. Miscellaneous cases

## Test Suite
| ID    | Title                         | Input                                                                                          |
| :---: | ----------------------------- | ---------------------------------------------------------------------------------------------- |
| 1.1   | Valid uuid and activate true  | `activate_agent {uuid:"<--valid agent uuid string-->", activate:true}`                         |
| 1.2   | Valid uuid and activate false | `activate_agent {uuid:"<--valid agent uuid string-->", activate:false}`                        |
| 2.1   | Missing uuid                  | `activate_agent {activate:true}`                                                               |
| 2.2.1 | Invalid uuid case 1           | `activate_agent {uuid:[], activate:true}`                                                      |
| 2.2.2 | Invalid uuid case 2           | `activate_agent {uuid:{}, activate:true}`                                                      |
| 2.2.3 | Invalid uuid case 3           | `activate_agent {uuid:"hahaha test test", activate:true}`                                      |
| 3.1   | Missing activate              | `activate_agent {uuid:"<--valid agent uuid string-->"}`                                        |
| 3.2.1 | Invalid activate case 1       | `activate_agent {uuid:"<--valid agent uuid string-->", activate:[]}`                           |
| 3.2.2 | Invalid activate case 2       | `activate_agent {uuid:"<--valid agent uuid string-->", activate:{}}`                           |
| 3.2.3 | Invalid activate case 3       | `activate_agent {uuid:"<--valid agent uuid string-->", activate:12345}`                        |
| 3.2.4 | Invalid activate case 4       | `activate_agent {uuid:"<--valid agent uuid string-->", activate:"not true"}`                   |
| 4.1   | Missing input                 | `activate_agent {}`                                                                            |
| 4.2   | Unexpected arguments          | `activate_agent {uuid:"<--valid agent uuid string-->", activate:true, something:"something?"}` |
| 4.3.1 | Malformed arguments case 1    | `activate_agent "testtest"`                                                                    |
| 4.3.2 | Malformed arguments case 2    | `activate_agent []`                                                                            |
| 4.3.3 | Malformed arguments case 3    | `activate_agent ;!/`                                                                           |
| 4.3.4 | Malformed arguments case 4    | `activate_agent }}}}`                                                                          |

## Expected Output
| ID    | Title                         | Result  | Payload                            | Comments                                        |
| :---: | ----------------------------- | :-----: | ---------------------------------- | ----------------------------------------------- |
| 1.1   | Valid uuid and activate true  | success | `{<--agent json object-->}`        |                                                 |
| 1.2   | Valid uuid and activate false | success | `{<--agent json object-->}`        |                                                 |
| 2.1   | Missing uuid                  | failure | `"uuid missing"`                   |                                                 |
| 2.2.1 | Invalid uuid case 1           | failure | `"uuid invalid"`                   |                                                 |
| 2.2.2 | Invalid uuid case 2           | failure | `"uuid invalid"`                   |                                                 |
| 2.2.3 | Invalid uuid case 3           | failure | `"uuid invalid"`                   |                                                 |
| 3.1   | Missing activate              | failure | `"activate missing"`               |                                                 |
| 3.2.1 | Invalid activate case 1       | failure | `"activate must be true or false"` |                                                 |
| 3.2.2 | Invalid activate case 2       | failure | `"activate must be true or false"` |                                                 |
| 3.2.3 | Invalid activate case 3       | failure | `"activate must be true or false"` |                                                 |
| 3.2.4 | Invalid activate case 4       | failure | `"activate must be true or false"` |                                                 |
| 4.1   | Missing input                 | failure | `"uuid missing"`                   |                                                 |
| 4.2   | Unexpected arguments          | success | `{<--agent json object-->}`        | Unexpected arguments should be ignored silently |
| 4.3.1 | Malformed arguments case 1    | failure | `"malformed arguments"`            |                                                 |
| 4.3.2 | Malformed arguments case 2    | failure | `"malformed arguments"`            |                                                 |
| 4.3.3 | Malformed arguments case 3    | failure | `"malformed arguments"`            |                                                 |
| 4.3.4 | Malformed arguments case 4    | failure | `"malformed arguments"`            |                                                 |
