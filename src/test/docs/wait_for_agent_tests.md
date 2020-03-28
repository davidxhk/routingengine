# Tests for `wait_for_agent`

## Contents
The following types of tests for `wait_for_agent` are provided in this test suite.
  1. Valid inputs
  2. Errors for uuid input
  3. Miscellaneous cases

## Test Suite
| ID    | Title                      | Input                                                                                             |
| :---: | -------------------------- | ------------------------------------------------------------------------------------------------- |
| 1.1   | Valid uuid                 | `wait_for_agent {uuid:"<--valid support request uuid string -->"}`                                |
| 2.1   | Missing uuid               | `wait_for_agent {}`                                                                               |
| 2.2.1 | Invalid uuid case 1        | `wait_for_agent {uuid:[]}`                                                                        |
| 2.2.2 | Invalid uuid case 2        | `wait_for_agent {uuid:{}}`                                                                        |
| 2.2.3 | Invalid uuid case 3        | `wait_for_agent {uuid:"hahaha test test"}`                                                        |
| 3.1   | Missing input              | `wait_for_agent {}`                                                                               |
| 3.2   | Unexpected arguments       | `wait_for_agent {uuid:"<--valid support request uuid string -->", something:"something?"}`        |
| 3.3.1 | Malformed arguments case 1 | `wait_for_agent "testtest"`                                                                       |
| 3.3.2 | Malformed arguments case 2 | `wait_for_agent []`                                                                               |
| 3.3.3 | Malformed arguments case 3 | `wait_for_agent ;!/`                                                                              |
| 3.3.4 | Malformed arguments case 4 | `wait_for_agent }}}}`                                                                             |

## Expected Output
| ID    | Title                      | Result  | Payload                               | Comments                                        |
| :---: | -------------------------- | :-----: | ------------------------------------- | ----------------------------------------------- |
| 1.1   | Valid uuid                 | success | `{<--support request json object-->}` |                                                 |
| 2.1   | Missing uuid               | failure | `"uuid missing"`                      |                                                 |
| 2.2.1 | Invalid uuid case 1        | failure | `"uuid invalid"`                      |                                                 |
| 2.2.2 | Invalid uuid case 2        | failure | `"uuid invalid"`                      |                                                 |
| 2.2.3 | Invalid uuid case 3        | failure | `"uuid invalid"`                      |                                                 |
| 3.1   | Missing input              | failure | `"uuid missing"`                      |                                                 |
| 3.2   | Unexpected arguments       | success | `{<--support request json object-->}` | Unexpected arguments should be ignored silently |
| 3.3.1 | Malformed arguments case 1 | failure | `"malformed arguments"`               |                                                 |
| 3.3.2 | Malformed arguments case 2 | failure | `"malformed arguments"`               |                                                 |
| 3.3.3 | Malformed arguments case 3 | failure | `"malformed arguments"`               |                                                 |
| 3.3.4 | Malformed arguments case 4 | failure | `"malformed arguments"`               |                                                 |
