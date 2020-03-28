# Tests for `update_agent_availability`

## Contents
The following types of tests for `update_agent_availability` are provided in this test suite.
  1. Valid inputs
  2. Errors for uuid input
  3. Errors for available input
  4. Miscellaneous cases

## Test Suite
| ID    | Title                                 | Input                                                                                                        |
| :---: | ------------------------------------- | ------------------------------------------------------------------------------------------------------------ |
| 1.1   | Valid uuid and available true         | `update_agent_availability {uuid:"<--valid agent uuid string -->", available:true}`                          |
| 1.2   | Valid uuid and available false        | `update_agent_availability {uuid:"<--valid agent uuid string -->", available:false}`                         |
| 2.1   | Missing uuid                          | `update_agent_availability {available:true}`                                                                 |
| 2.2.1 | Invalid uuid case 1                   | `update_agent_availability {uuid:[], available:true}`                                                        |
| 2.2.2 | Invalid uuid case 2                   | `update_agent_availability {uuid:{}, available:true}`                                                        |
| 2.2.3 | Invalid uuid case 3                   | `update_agent_availability {uuid:"hahaha test test", available:true}`                                        |
| 3.1   | Missing available                     | `update_agent_availability {uuid:"<--valid agent uuid string -->"}`                                          |
| 3.2.1 | Invalid available case 1              | `update_agent_availability {uuid:"<--valid agent uuid string -->", available:[]}`                            |
| 3.2.2 | Invalid available case 2              | `update_agent_availability {uuid:"<--valid agent uuid string -->", available:{}}`                            |
| 3.2.3 | Invalid available case 3              | `update_agent_availability {uuid:"<--valid agent uuid string -->", available:12345}`                         |
| 3.2.4 | Invalid available case 4              | `update_agent_availability {uuid:"<--valid agent uuid string -->", available:"not true"}`                    |
| 4.1   | Missing input                         | `update_agent_availability {}`                                                                               |
| 4.2   | Unexpected arguments                  | `update_agent_availability {uuid:"<--valid agent uuid string -->", available:true, something:"something?"}`  |
| 4.3.1 | Malformed arguments case 1            | `update_agent_availability "testtest"`                                                                       |
| 4.3.2 | Malformed arguments case 2            | `update_agent_availability []`                                                                               |
| 4.3.3 | Malformed arguments case 3            | `update_agent_availability ;!/`                                                                              |
| 4.3.4 | Malformed arguments case 4            | `update_agent_availability }}}}`                                                                             |

## Expected Output
| ID    | Title                                 | Result  | Payload                                          | Comments                                        |
| :---: | ------------------------------------- | :-----: | ------------------------------------------------ | ----------------------------------------------- |
| 1.1   | Valid uuid and available true         | success | `{<--agent json object-->}`                      |                                                 |
| 1.2   | Valid uuid and available false        | success | `{<--agent json object-->}`                      |                                                 |
| 2.1   | Missing uuid                          | failure | `"uuid missing"`                                 |                                                 |
| 2.2.1 | Invalid uuid case 1                   | failure | `"uuid invalid"`                                 |                                                 |
| 2.2.2 | Invalid uuid case 2                   | failure | `"uuid invalid"`                                 |                                                 |
| 2.2.3 | Invalid uuid case 3                   | failure | `"uuid invalid"`                                 |                                                 |
| 3.1   | Missing available                     | failure | `"available missing"`                            |                                                 |
| 3.2.1 | Invalid available case 1              | failure | `"available must be true or false"`              |                                                 |
| 3.2.2 | Invalid available case 2              | failure | `"available must be true or false"`              |                                                 |
| 3.2.3 | Invalid available case 3              | failure | `"available must be true or false"`              |                                                 |
| 3.2.4 | Invalid available case 4              | failure | `"available must be true or false"`              |                                                 |
| 4.1   | Missing input                         | failure | `"uuid missing"`                                 |                                                 |
| 4.2   | Unexpected arguments                  | success | `{<--agent json object-->}`                      | Unexpected arguments should be ignored silently |
| 4.3.1 | Malformed arguments case 1            | failure | `"malformed arguments"`                          |                                                 |
| 4.3.2 | Malformed arguments case 2            | failure | `"malformed arguments"`                          |                                                 |
| 4.3.3 | Malformed arguments case 3            | failure | `"malformed arguments"`                          |                                                 |
| 4.3.4 | Malformed arguments case 4            | failure | `"malformed arguments"`                          |                                                 |
