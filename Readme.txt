Project by Ngurinzira Pascal

# Product Management GraphQL Service
--This is a Spring boot Project to manage the Products.

This GraphQL-based service provides functionalities for managing products, orders, users, and product lines within a product management system.

## Functionality

### User Management
- Create, update, retrieve, and delete users.
- Assign roles to users for access control.

### Product Management
- Create, update, retrieve, and delete products.
- Manage product stock levels.

### Product Line Management
- Create, update, and delete product lines.
- Associate product lines with specific products and manage quantities.

### Order Management
- Create, update, retrieve, and delete orders.
- Manage order details and associated product lines.

--**For create an order create all product lines and pass the product line ids in the productLineIds array section of JSON,
--**For updating an Order Just replace the new product line ids and request for update, it will manage stocks automatically.

### Security
- Authentication and authorization using Spring Security.
- Role-based access control for admin and user roles using JWT token.

### GraphQL Resolvers
- Implements GraphQL resolvers for querying and mutating data.
- Supports pagination for efficient data retrieval.

### Miscellaneous
- Default admin user creation for initial system setup.

## Getting Started

- Install JAVA: https://www.java.com/en/download/

- Install Tomcat:
    Linux: https://www.digitalocean.com/community/tutorials/how-to-install-apache-tomcat-8-on-ubuntu-16-04
    Mac: https://wolfpaulus.com/mac/tomcat/ or use homebrew formula: brew install tomcat (https://formulae.brew.sh/formula/tomcat)
    Windows: https://www.programmergate.com/step-by-step-guide-for-installing-tomcat-on-windows/ and more details on https://tomcat.apache.org/

- Clone the repository and install dependencies.
- In Postman folder, there is a documentation. It contains a json collection to be imported in postman or any other tools (Postman is recommended)
- Configure database settings and security configurations.
- Build and run the application locally.
- The application use default port for tomcat 8080, it is available on localhost:8080/graphql
- Access the DB console through http://localhost:8080/h2-console/
- Default user credentials : email: admin@gmail.com password: admin

If more information is needed, kindly contact me on ngurinzira@gmail.com

