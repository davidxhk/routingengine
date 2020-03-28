# Tests for `new_agent`

## Contents
The following types of tests for `new_agent` are provided in this test suite.
  1. Valid inputs
  2. Errors for skills input
  3. Errors for address input
  4. Miscellaneous cases

## Test Suite
| ID    | Title                      | Input                                                                                                                         |
| :---: | -------------------------- | ----------------------------------------------------------------------------------------------------------------------------- |
| 1.1   | Skill with type index      | `new_agent {skills:{1:true, 2:true}}`                                                                                         |
| 1.2   | Skill with type string     | `new_agent {skills:{"GENERAL_ENQUIRY":true, "CHECK_BILL":true}}`                                                              |
| 1.3   | Skill and address          | `new_agent {skills:{"GENERAL_ENQUIRY":true}, address:127.0.0.1}`                                                              |
| 2.1.1 | Missing skill case 1       | `new_agent {}`                                                                                                                |
| 2.1.2 | Missing skill case 2       | `new_agent {skills:{999:true,-1:true}}`                                                                                       |
| 2.1.3 | Missing skill case 3       | `new_agent {skills:{TEST:true,hmmmm:true}}`                                                                                   |
| 2.1.4 | Missing skill case 4       | `new_agent {skills:{0:false,1:false,2:false}}`                                                                                |
| 2.2.1 | Invalid skills case 1      | `new_agent {skills:{1:"haha"}}`                                                                                               |
| 2.2.2 | Invalid skills case 2      | `new_agent {skills:{GENERAL_ENQUIRY:"no"}}`                                                                                   |
| 2.2.3 | Invalid skills case 3      | `new_agent {skills:2}`                                                                                                        |
| 2.2.4 | Invalid skills case 4      | `new_agent {skills:"zz"}`                                                                                                     |
| 2.2.5 | Invalid skills case 5      | `new_agent {skills:[1,2,3,4]}`                                                                                                |
| 3.1.1 | Invalid address case 1     | `new_agent {skills:{1:true, 2:true}, address:[]}`                                                                             |
| 3.1.2 | Invalid address case 2     | `new_agent {skills:{1:true, 2:true}, address={}}`                                                                             |
| 3.1.3 | Invalid address case 3     | `new_agent {skills:{1:true, 2:true}, address="adsflasdfmlsdf"}`                                                               |
| 3.1.4 | Invalid address case 4     | `new_agent {skills:{1:true, 2:true}, address="999.999.999.999"}`                                                              |
| 4.1   | Missing input              | `new_agent {}`                                                                                                                |
| 4.2   | Unexpected arguments       | `new_agent {skills:{1:true, 2:true}, something:"something?"}`                                                                 |
| 4.3.1 | Malformed arguments case 1 | `new_agent "testtest"`                                                                                                        |
| 4.3.2 | Malformed arguments case 2 | `new_agent []`                                                                                                                |
| 4.3.3 | Malformed arguments case 3 | `new_agent ;!/`                                                                                                               |
| 4.3.4 | Malformed arguments case 4 | `new_agent }}}}`                                                                                                              |

## Expected Output
| ID    | Title                      | Result  | Payload                                          | Comments                                                         |
| :---: | -------------------------- | :-----: | ------------------------------------------------ | ---------------------------------------------------------------- |
| 1.1   | Skill with type index      | success | `{<--agent json object-->}`                      |                                                                  |
| 1.2   | Skill with type string     | success | `{<--agent json object-->}`                      |                                                                  |
| 1.3   | Skill and address          | success | `{<--agent json object-->}`                      | Address field is optional, defaults to the socket's host address |
| 2.1.1 | Missing skill case 1       | failure | `"skills missing"`                               |                                                                  |
| 2.1.2 | Missing skill case 2       | failure | `"skill missing"`                                | Invalid skill type index should be ignored silently              |
| 2.1.3 | Missing skill case 3       | failure | `"skill missing"`                                | Invalid skill type string should be ignored silently             |
| 2.1.4 | Missing skill case 4       | failure | `"skill missing"`                                | At least one out of all skills must be set to true               |
| 2.2.1 | Invalid skills case 1      | failure | `"skills 1 must be true or false"`               |                                                                  |
| 2.2.2 | Invalid skills case 2      | failure | `"skills GENERAL_ENQUIRY must be true or false"` |                                                                  |
| 2.2.3 | Invalid skills case 3      | failure | `"skills invalid"`                               |                                                                  |
| 2.2.4 | Invalid skills case 4      | failure | `"skills invalid"`                               |                                                                  |
| 2.2.5 | Invalid skills case 5      | failure | `"skills invalid"`                               |                                                                  |
| 3.1.1 | Invalid address case 1     | failure | `"address invalid"`                              |                                                                  |
| 3.1.2 | Invalid address case 2     | failure | `"address invalid"`                              |                                                                  |
| 3.1.3 | Invalid address case 3     | failure | `"address invalid"`                              |                                                                  |
| 3.1.4 | Invalid address case 4     | failure | `"address invalid"`                              |                                                                  |
| 4.1   | Missing input              | failure | `"skills missing"`                               |                                                                  |
| 4.2   | Unexpected arguments       | success | `{<--agent json object-->}`                      | Unexpected arguments should be ignored silently                  |
| 4.3.1 | Malformed arguments case 1 | failure | `"malformed arguments"`                          |                                                                  |
| 4.3.2 | Malformed arguments case 2 | failure | `"malformed arguments"`                          |                                                                  |
| 4.3.3 | Malformed arguments case 3 | failure | `"malformed arguments"`                          |                                                                  |
| 4.3.4 | Malformed arguments case 4 | failure | `"malformed arguments"`                          |                                                                  |
