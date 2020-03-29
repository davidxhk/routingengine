# Tests for `get_support_request_status`

## Contents
The following types of tests for `get_support_request_status` are provided in this test suite.
  1. Valid inputs
  2. Miscellaneous cases

## Test Suite
|   ID  | Title                      | Input                                                                                                    |
| :---: | -------------------------- | -------------------------------------------------------------------------------------------------------- |
|  1.1  | No arguments               | `get_support_request_status {}`                                                                          |
|  2.1  | Unexpected arguments       | `get_support_request_status {something:"something?"}`                                                    |
| 2.2.1 | Malformed arguments case 1 | `get_support_request_status "testtest"`                                                                  |
| 2.2.2 | Malformed arguments case 2 | `get_support_request_status []`                                                                          |
| 2.2.3 | Malformed arguments case 3 | `get_support_request_status ;!/`                                                                         |
| 2.2.4 | Malformed arguments case 4 | `get_support_request_status }}}}`                                                                        |

## Expected Output
|   ID  | Title                      |  Result | Payload                                      | Comments                                        |
| :---: | -------------------------- | :-----: | -------------------------------------------- | ----------------------------------------------- |
|  1.1  | No arguments               | success | `{<--support request status json object-->}` |                                                 |
|  2.1  | Unexpected arguments       | success | `{<--support request status json object-->}` | Unexpected arguments should be ignored silently |
| 2.2.1 | Malformed arguments case 1 | failure | `"malformed arguments"`                      |                                                 |
| 2.2.2 | Malformed arguments case 2 | failure | `"malformed arguments"`                      |                                                 |
| 2.2.3 | Malformed arguments case 3 | failure | `"malformed arguments"`                      |                                                 |
| 2.2.4 | Malformed arguments case 4 | failure | `"malformed arguments"`                      |                                                 |
