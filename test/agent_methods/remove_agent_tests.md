# Tests for `remove_agent`

## Contents
The following types of tests for `remove_agent` are provided in this test suite.
  1. Valid inputs
  2. Errors for uuid input
  3. Miscellaneous cases

## Test Suite
| ID    | Title                      | Input                                                                                   |
| :---: | -------------------------- | --------------------------------------------------------------------------------------- |
| 1.1   | Valid uuid                 | `remove_agent {uuid:"<--valid agent uuid string -->"}`                                  |
| 2.1   | Missing uuid               | `remove_agent {}`                                                                       |
| 2.2.1 | Invalid uuid case 1        | `remove_agent {uuid:[]}`                                                                |
| 2.2.2 | Invalid uuid case 2        | `remove_agent {uuid:{}}`                                                                |
| 2.2.3 | Invalid uuid case 3        | `remove_agent {uuid:"hahaha test test"}`                                                |
| 3.1   | Missing input              | `remove_agent {}`                                                                       |
| 3.2   | Unexpected arguments       | `remove_agent {uuid:"<--valid agent uuid string -->", something:"something?"}`          |
| 3.3.1 | Malformed arguments case 1 | `remove_agent "testtest"`                                                               |
| 3.3.2 | Malformed arguments case 2 | `remove_agent []`                                                                       |
| 3.3.3 | Malformed arguments case 3 | `remove_agent ;!/`                                                                      |
| 3.3.4 | Malformed arguments case 4 | `remove_agent }}}}`                                                                     |

## Expected Output
| ID    | Title                      | Result  | Payload                     | Comments                                        |
| :---: | -------------------------- | :-----: | --------------------------- | ----------------------------------------------- |
| 1.1   | Valid uuid                 | success | `{<--agent json object-->}` |                                                 |
| 2.1   | Missing uuid               | failure | `"uuid missing"`            |                                                 |
| 2.2.1 | Invalid uuid case 1        | failure | `"uuid invalid"`            |                                                 |
| 2.2.2 | Invalid uuid case 2        | failure | `"uuid invalid"`            |                                                 |
| 2.2.3 | Invalid uuid case 3        | failure | `"uuid invalid"`            |                                                 |
| 3.1   | Missing input              | failure | `"uuid missing"`            |                                                 |
| 3.2   | Unexpected arguments       | success | `{<--agent json object-->}` | Unexpected arguments should be ignored silently |
| 3.3.1 | Malformed arguments case 1 | failure | `"malformed arguments"`     |                                                 |
| 3.3.2 | Malformed arguments case 2 | failure | `"malformed arguments"`     |                                                 |
| 3.3.3 | Malformed arguments case 3 | failure | `"malformed arguments"`     |                                                 |
| 3.3.4 | Malformed arguments case 4 | failure | `"malformed arguments"`     |                                                 |
