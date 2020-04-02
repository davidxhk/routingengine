# Compiled Valid Commands

### User Related Commands

|         Method         | Command                                                                                |
| :--------------------: | :------------------------------------------------------------------------------------- |
|  New Support Request   | `new_support_request {name:"bob", email:"bob@abc.com", type:1}`                        |
|  New Support Request   | `new_support_request {name:"bob", email:"bob@abc.com", type:"GENERAL_ENQUIRY"}`        |
|  New Support Request   | `new_support_request {name:"bob", email:"bob@abc.com", type:1, address:127.0.0.1}`     |
|     Wait For Agent     | `wait_for_agent {uuid:"<--valid support request uuid string-->"}`                      |
| Change Support Request | `change_support_request_type {uuid:"<--valid support request uuid string-->", type:1}` |
| Change Support Request | `change_support_request_type {uuid:"<--valid support request uuid string-->", type:"GENERAL_ENQUIRY"}` |
| Close Support Request  | `close_support_request {uuid:"<--valid support request uuid string-->"}`               |

### Agent Related Commnds

|          Method           | Command                                                                             |
| :-----------------------: | :---------------------------------------------------------------------------------- |
|         New Agent         | `new_agent {skills:{1:true, 2:true}}`                                               |
|         New Agent         | `new_agent {skills:{"GENERAL_ENQUIRY":true, "CHECK_BILL":true}}`                    |
|         New Agent         | `new_agent {skills:{"GENERAL_ENQUIRY":true}, address:127.0.0.1}`                    |
| Update Agent Availability | `update_agent_availability {uuid:"<--valid agent uuid string-->", available:true}`  |
| Update Agent Availability | `update_agent_availability {uuid:"<--valid agent uuid string-->", available:false}` |
|   Take Support Request    | `take_support_request {uuid:"<--valid agent uuid string-->"}`                       |
|   Drop Support Request    | `drop_support_request {uuid:"<--valid agent uuid string-->"}`                       |

### Admin Related Commands

|             Method              | Command                                                                   |
| :-----------------------------: | :------------------------------------------------------------------------ |
|              Ping               | `ping {}`                                                                 |
| Get All Support Requests Status | `get_support_request_status {}`                                           |
|  Check Support Request Status   | `check_support_request {uuid:"<--valid support request uuid string-->"}`  |
|      Close Support Request      | `close_support_request {uuid:"<--valid support request uuid string-->"}`  |
|     Remove Support Request      | `remove_support_request {uuid:"<--valid support request uuid string-->"}` |
|      Get All Agent Status       | `get_agent_status {}`                                                     |
|       Check Agent Status        | `check_agent {uuid:"<--valid agent uuid string-->"}`                      |
|         Activate Agent          | `activate_agent {uuid:"<--valid agent uuid string-->", activate:true}`    |
|         Activate Agent          | `activate_agent {uuid:"<--valid agent uuid string-->", activate:false}`    |
|          Remove Agent           | `remove_agent {uuid:"<--valid agent uuid string-->"}`                     |
|        Get Queue Status         | `get_queue_status {}`                                                     |
|       Get Status Overview       | `get_status_overview {}`                                                  |
