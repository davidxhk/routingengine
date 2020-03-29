# Tests for `update_agent_skills`

## Contents
The following types of tests for `update_agent_skills` are provided in this test suite.
  1. Valid inputs
  2. Errors for uuid input
  3. Errors for skills input
  4. Miscellaneous cases

## Test Suite
| ID    | Title                                 | Input                                                                                                             |
| :---: | ------------------------------------- | ----------------------------------------------------------------------------------------------------------------- |
| 1.1   | Valid uuid and skill with type index  | `update_agent_skills {uuid:"<--valid agent uuid string-->", skills:{1:true, 2:true}}`                             |
| 1.2   | Valid uuid and skill with type string | `update_agent_skills {uuid:"<--valid agent uuid string-->", skills:{"GENERAL_ENQUIRY":true}}`                     |
| 2.1   | Missing uuid                          | `update_agent_skills {skills:{"GENERAL_ENQUIRY":true}}`                                                           |
| 2.2.1 | Invalid uuid case 1                   | `update_agent_skills {uuid:[], skills:{1:true, 2:true}}`                                                          |
| 2.2.2 | Invalid uuid case 2                   | `update_agent_skills {uuid:{}, skills:{1:true, 2:true}}`                                                          |
| 2.2.3 | Invalid uuid case 3                   | `update_agent_skills {uuid:"hahaha test test", skills:{1:true, 2:true}}`                                          |
| 3.1.1 | Missing skill case 1                  | `update_agent_skills {uuid:"<--valid agent uuid string-->"}`                                                      |
| 3.1.2 | Missing skill case 2                  | `update_agent_skills {uuid:"<--valid agent uuid string-->", skills:{999:true,-1:true}}`                           |
| 3.1.3 | Missing skill case 3                  | `update_agent_skills {uuid:"<--valid agent uuid string-->", skills:{TEST:true,hmmmm:true}}`                       |
| 3.1.4 | Missing skill case 4                  | `update_agent_skills {uuid:"<--valid agent uuid string-->", skills:{0:false,1:false,2:false}}`                    |
| 3.2.1 | Invalid skills case 1                 | `update_agent_skills {uuid:"<--valid agent uuid string-->", skills:{1:"haha"}}`                                   |
| 3.2.2 | Invalid skills case 2                 | `update_agent_skills {uuid:"<--valid agent uuid string-->", skills:{GENERAL_ENQUIRY:"no"}}`                       |
| 3.2.3 | Invalid skills case 3                 | `update_agent_skills {uuid:"<--valid agent uuid string-->", skills:2}`                                            |
| 3.2.4 | Invalid skills case 4                 | `update_agent_skills {uuid:"<--valid agent uuid string-->", skills:"zz"}`                                         |
| 3.2.5 | Invalid skills case 5                 | `update_agent_skills {uuid:"<--valid agent uuid string-->", skills:[1,2,3,4]}`                                    |
| 4.1   | Missing input                         | `update_agent_skills {}`                                                                                          |
| 4.2   | Unexpected arguments                  | `update_agent_skills {uuid:"<--valid agent uuid string-->", skills:{1:true, 2:true}, something:"something?"}`     |
| 4.3.1 | Malformed arguments case 1            | `update_agent_skills "testtest"`                                                                                  |
| 4.3.2 | Malformed arguments case 2            | `update_agent_skills []`                                                                                          |
| 4.3.3 | Malformed arguments case 3            | `update_agent_skills ;!/`                                                                                         |
| 4.3.4 | Malformed arguments case 4            | `update_agent_skills }}}}`                                                                                        |

## Expected Output
| ID    | Title                                 | Result  | Payload                                          | Comments                                             |
| :---: | ------------------------------------- | :-----: | ------------------------------------------------ | ---------------------------------------------------- |
| 1.1   | Valid uuid and skill with type index  | success | `{<--agent json object-->}`                      |                                                      |
| 1.2   | Valid uuid and skill with type string | success | `{<--agent json object-->}`                      |                                                      |
| 2.1   | Missing uuid                          | failure | `"uuid missing"`                                 |                                                      |
| 2.2.1 | Invalid uuid case 1                   | failure | `"uuid invalid"`                                 |                                                      |
| 2.2.2 | Invalid uuid case 2                   | failure | `"uuid invalid"`                                 |                                                      |
| 2.2.3 | Invalid uuid case 3                   | failure | `"uuid invalid"`                                 |                                                      |
| 3.1.1 | Missing skill case 1                  | failure | `"skills missing"`                               |                                                      |
| 3.1.2 | Missing skill case 2                  | failure | `"skill missing"`                                | Invalid skill type index should be ignored silently  |
| 3.1.3 | Missing skill case 3                  | failure | `"skill missing"`                                | Invalid skill type string should be ignored silently |
| 3.1.4 | Missing skill case 4                  | failure | `"skill missing"`                                | At least one out of all skills must be set to true   |
| 3.2.1 | Invalid skills case 1                 | failure | `"skills 1 must be true or false"`               |                                                      |
| 3.2.2 | Invalid skills case 2                 | failure | `"skills GENERAL_ENQUIRY must be true or false"` |                                                      |
| 3.2.3 | Invalid skills case 3                 | failure | `"skills invalid"`                               |                                                      |
| 3.2.4 | Invalid skills case 4                 | failure | `"skills invalid"`                               |                                                      |
| 3.2.5 | Invalid skills case 5                 | failure | `"skills invalid"`                               |                                                      |
| 4.1   | Missing input                         | failure | `"uuid missing"`                                 |                                                      |
| 4.2   | Unexpected arguments                  | success | `{<--agent json object-->}`                      | Unexpected arguments should be ignored silently      |
| 4.3.1 | Malformed arguments case 1            | failure | `"malformed arguments"`                          |                                                      |
| 4.3.2 | Malformed arguments case 2            | failure | `"malformed arguments"`                          |                                                      |
| 4.3.3 | Malformed arguments case 3            | failure | `"malformed arguments"`                          |                                                      |
| 4.3.4 | Malformed arguments case 4            | failure | `"malformed arguments"`                          |                                                      |
