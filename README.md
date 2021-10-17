# Rabobank Assignment for Authorizations Area

The service is responsible for granting and revoking access to clients accounts. 

## Possible improvements
- Introduce Spring Security to establish users authentication and authorization rules.
- Business logic should be discussed with Domain expert.
- Improve MongoDB data model.
## How to run it locally (via Docker-compose)
1.  build the project

```bash
    ./mvnw clean install
```
2.  change to application directory

```bash
    cd api
```
3. build a docker image
```bash
    ./build.sh
   ```

4. run dockerized service `docker-compose.yml`

```bash
    docker-compose up
```
   
5. The service is available on `http://localhost:8080`

### Use case example

```bash
-- Create A-001 account
curl --location --request POST 'localhost:8080/clients/TV-001/accounts' \
--header 'Content-Type: application/json' \
--data-raw '{
    "accountNumber": "A-001",
    "type":"PAYMENT"
}'

-- Create A-002 account
curl --location --request POST 'localhost:8080/clients/TV-001/accounts' \
--header 'Content-Type: application/json' \
--data-raw '{
    "accountNumber": "A-002",
    "type":"PAYMENT"
}'

-- Get all accounts for client TV_002 -> No accounts
curl --location --request GET 'localhost:8080/clients/TV-002/accounts' \
--header 'Content-Type: application/json'

-- Grant powerOfAttorney TV-001:[A-001] -> TV-002
curl --location --request POST 'localhost:8080/clients/TV-001/accounts/A-001/powerOfAttorney' \
--header 'Content-Type: application/json' \
--data-raw '{
    "granteeName": "TV-002",
    "authorization":"WRITE"
}'

-- Get all accounts for client TV_002 -> A-001 account returned
curl --location --request GET 'localhost:8080/clients/TV-002/accounts' \
--header 'Content-Type: application/json'

-- Revoke powerOfAttorney TV-001:[A-001] -> TV-002
curl --location --request DELETE 'localhost:8080/clients/TV-001/accounts/A-001/powerOfAttorney?granteeName=TV-002&authorization=WRITE' \
--header 'Content-Type: application/json'

-- Get all accounts for client TV_002 -> No accounts
curl --location --request GET 'localhost:8080/clients/TV-002/accounts' \
--header 'Content-Type: application/json'
```