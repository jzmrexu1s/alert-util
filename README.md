# alert-util
An module that can alert the certain content by the frequency you designate.
## Usage
Invoke function `addLimitAlert` to create an alert.  
Parameters:  
`String key`: Necessary. Alerts with identical `key` will be actually send when the quantity of alerts does not reach `expireCount` before this timestamp: the timestamp first alert with this `key` came plus  `expireTime`. Other alerts will be ignored.  
`String content`: Necessary. Defines the content of alert.  
`int expireTime`: Optional.  
`int expireCount`: Optional.  