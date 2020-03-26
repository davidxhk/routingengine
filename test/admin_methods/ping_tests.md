# Tests for `ping`

## Contents
The following types of tests for `ping` are provided in this test suite.
  1. Valid inputs
  2. Miscellaneous cases

## Test Suite
|   ID  | Title                      | Input                                                                               |
|:-----:|----------------------------|-------------------------------------------------------------------------------------|
|  1.1  | Ping                       | `ping {}`                                                                           |
|  2.1  | Unexpected arguments       | `ping {something:"something?"}`                                                     |
| 2.2.1 | Malformed arguments case 1 | `ping "testtest"`                                                                   |
| 2.2.2 | Malformed arguments case 2 | `ping []`                                                                           |
| 2.2.3 | Malformed arguments case 3 | `ping ;!/`                                                                          |
| 2.2.4 | Malformed arguments case 4 | `ping }}}}`                                                                         |

## Expected Output
|   ID  | Title                      |  Result | Payload                 | Comments                                        |
|:-----:|----------------------------|:-------:|-------------------------|-------------------------------------------------|
|  1.1  | Ping                       | success | `"pong"`                |                                                 |
|  2.1  | Unexpected arguments       | success | `"pong"`                | Unexpected arguments should be ignored silently |
| 2.2.1 | Malformed arguments case 1 | failure | `"malformed arguments"` |                                                 |
| 2.2.2 | Malformed arguments case 2 | failure | `"malformed arguments"` |                                                 |
| 2.2.3 | Malformed arguments case 3 | failure | `"malformed arguments"` |                                                 |
| 2.2.4 | Malformed arguments case 4 | failure | `"malformed arguments"` |                                                 |
