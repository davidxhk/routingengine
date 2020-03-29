# Tests for `get_agent_status`

## Contents
The following types of tests for `get_agent_status` are provided in this test suite.
  1. Valid inputs
  2. Miscellaneous cases

## Test Suite
|   ID  | Title                      | Input                                                                                          |
| :---: | -------------------------- | ---------------------------------------------------------------------------------------------- |
|  1.1  | No arguments               | `get_agent_status {}`                                                                          |
|  2.1  | Unexpected arguments       | `get_agent_status {something:"something?"}`                                                    |
| 2.2.1 | Malformed arguments case 1 | `get_agent_status "testtest"`                                                                  |
| 2.2.2 | Malformed arguments case 2 | `get_agent_status []`                                                                          |
| 2.2.3 | Malformed arguments case 3 | `get_agent_status ;!/`                                                                         |
| 2.2.4 | Malformed arguments case 4 | `get_agent_status }}}}`                                                                        |

## Expected Output
|   ID  | Title                      |  Result | Payload                            | Comments                                        |
| :---: | -------------------------- | :-----: | ---------------------------------- | ----------------------------------------------- |
|  1.1  | No arguments               | success | `{<--agent status json object-->}` |                                                 |
|  2.1  | Unexpected arguments       | success | `{<--agent status json object-->}` | Unexpected arguments should be ignored silently |
| 2.2.1 | Malformed arguments case 1 | failure | `"malformed arguments"`            |                                                 |
| 2.2.2 | Malformed arguments case 2 | failure | `"malformed arguments"`            |                                                 |
| 2.2.3 | Malformed arguments case 3 | failure | `"malformed arguments"`            |                                                 |
| 2.2.4 | Malformed arguments case 4 | failure | `"malformed arguments"`            |                                                 |
