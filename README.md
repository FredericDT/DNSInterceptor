# DNSInterceptor

## CommandLineArguments

- `--target-address=8.8.8.8`  
    relay target address

- `--target-port=53`  
    relay target port

- `--bind-address=127.0.0.1`  
    listen address

- `--bind-port=5153`  
    listen port

- `--file=<path>`  
    intercept file path
    
    example:
    ```
    0.0.0.0 a.com
    192.168.1.10 b.com
    0.0.0.0 www.google.com
    ```