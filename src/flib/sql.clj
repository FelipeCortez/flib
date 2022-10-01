(ns flib.sql
  (:require [next.jdbc :as jdbc]
            [next.jdbc.connection :as connection])
  (:import [com.zaxxer.hikari HikariDataSource]))

(def password (slurp "secrets/postgres-password"))

(def db (str "jdbc:postgresql://localhost:5432/bmarks?user=postgres&password=" password))

(def ds
  (connection/->pool com.zaxxer.hikari.HikariDataSource
                     {:dbtype "postgres"
                      :dbname "bmarks"
                      :username "postgres"
                      :password password
                      :dataSourceProperties {:socketTimeout 30}}))

(comment
  (jdbc/execute! ds ["
drop table address
"])

  (jdbc/execute! ds ["
create table address (
  id int primary key,
  name varchar(32),
  email varchar(255)
)"])

  (jdbc/execute! ds ["
insert into address(id,name,email)
  values(0, 'Sean Corfield','sean@corfield.org')"])

  (jdbc/execute! ds ["select * from address"])
  )
