-- The DLL

-- The Table Drops
SET FOREIGN_KEY_CHECKS=0
;
DROP TABLE IF EXISTS users_required_fields,
users,
creators,
creators_required_fields,
users_btc_addresses,
creators_btc_addresses,
pieces_issued,
pieces_owned,
bids,
asks,
sales_from_users,
sales_from_creators,
rewards,
rewards_earned,
host_btc_addresses,
fees,
creators_page_fields
;
DROP VIEW IF EXISTS prices, worth, candlestick_prices, rewards_annualized_pct, pieces_total, pieces_available, pieces_owned_total, users_current_view,
ask_bid_accept_checker, pieces_owned_accum, pieces_owned_value
;
SET FOREIGN_KEY_CHECKS=1
;
-- The Table creates and indexes
CREATE TABLE users
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   username VARCHAR(56) UNIQUE NOT NULL,
   password_encrypted TINYTEXT NOT NULL,
   email VARCHAR(56) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;

CREATE TABLE creators
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   username VARCHAR(56) UNIQUE NOT NULL,
   password_encrypted TINYTEXT NOT NULL,
   email VARCHAR(56) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;


CREATE TABLE creators_page_fields
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   creators_id int(11) UNIQUE NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   main_body TEXT NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;

CREATE TABLE users_btc_addresses
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   users_id int(11) NOT NULL,
   FOREIGN KEY (users_id) REFERENCES users(id),
   btc_addr VARCHAR(56) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;

CREATE TABLE host_btc_addresses
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   btc_addr VARCHAR(56) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;

CREATE TABLE creators_btc_addresses
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   btc_addr VARCHAR(56) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;
CREATE TABLE pieces_issued
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   time_ DATETIME NOT NULL,
   pieces_issued BIGINT(8) UNSIGNED NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;
CREATE TABLE pieces_owned
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   owners_id int(11) NOT NULL,
   FOREIGN KEY (owners_id) REFERENCES users(id),
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   time_ DATETIME NOT NULL,
   pieces_owned BIGINT(8) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;
CREATE TABLE bids
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   users_id int(11) NOT NULL,
   FOREIGN KEY (users_id) REFERENCES users(id),
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   time_ DATETIME NOT NULL,
   valid_until DATETIME NOT NULL,
   partial_fill TINYINT(1) NOT NULL,
   pieces BIGINT(8) UNSIGNED NOT NULL,
   bid_per_piece DOUBLE UNSIGNED NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;
CREATE TABLE asks
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   users_id int(11) NOT NULL,
   FOREIGN KEY (users_id) REFERENCES users(id),
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   time_ DATETIME NOT NULL,
   valid_until DATETIME NOT NULL,
   partial_fill TINYINT(1) NOT NULL,
   pieces BIGINT(8) UNSIGNED NOT NULL,
   ask_per_piece DOUBLE UNSIGNED NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;
CREATE TABLE sales_from_users
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   from_users_btc_addr_id int(11) NOT NULL,
   FOREIGN KEY (from_users_btc_addr_id) REFERENCES users_btc_addresses(id),
   to_users_btc_addr_id int(11) NOT NULL,
   FOREIGN KEY (to_users_btc_addr_id) REFERENCES users_btc_addresses(id),
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   time_ DATETIME NOT NULL,
   pieces BIGINT(8) UNSIGNED NOT NULL,
   price DOUBLE UNSIGNED NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;
CREATE TABLE sales_from_creators
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   from_creators_btc_addr_id int(11) NOT NULL,
   FOREIGN KEY (from_creators_btc_addr_id) REFERENCES creators_btc_addresses(id),
   to_users_btc_addr_id int(11) NOT NULL,
   FOREIGN KEY (to_users_btc_addr_id) REFERENCES users_btc_addresses(id),
   time_ DATETIME NOT NULL,
   pieces BIGINT(8) UNSIGNED NOT NULL,
   price DOUBLE UNSIGNED NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;
CREATE TABLE fees
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   sales_from_creators_id int(11) NOT NULL,
   host_btc_addr_id int(11) NOT NULL,
   FOREIGN KEY (host_btc_addr_id) REFERENCES host_btc_addresses(id),
   fee DOUBLE UNSIGNED NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;



CREATE TABLE rewards
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   time_ DATETIME NOT NULL,
   reward_amount DOUBLE UNSIGNED NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;
CREATE TABLE rewards_earned
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   users_id int(11) NOT NULL,
   FOREIGN KEY (users_id) REFERENCES users(id),
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   time_ DATETIME NOT NULL,
   amount DOUBLE UNSIGNED NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;



-- Views

CREATE VIEW prices AS
SELECT
creators_id, time_, price/pieces as price_per_piece
FROM sales_from_users
union
SELECT
creators_id, time_, price/pieces as price_per_piece
FROM sales_from_creators
inner join creators_btc_addresses
on sales_from_creators.from_creators_btc_addr_id = creators_btc_addresses.id
order by creators_id, time_
;


CREATE VIEW worth AS
SELECT
prices.creators_id, prices.time_, price_per_piece*pieces_issued as worth
FROM prices
inner join
pieces_issued
on prices.creators_id = pieces_issued.creators_id
where pieces_issued.time_ <= prices.time_
order by time_
;

-- TODO this should be done within the correct time frame
CREATE VIEW rewards_annualized_pct AS
SELECT
rewards.time_, rewards.creators_id, reward_amount*12*100/price_per_piece as rewards_annualized_pct
from rewards
inner join prices
on prices.creators_id = rewards.creators_id;

CREATE VIEW pieces_total as
select 
creators_id, sum(pieces_issued) as pieces_total
from pieces_issued
group by creators_id;

CREATE VIEW pieces_owned_total as
select 
pieces_owned.owners_id, pieces_owned.creators_id, sum(pieces_owned.pieces_owned) as pieces_owned_total
from pieces_owned
group by owners_id, creators_id;



-- Pieces available is the total pieces - pieces_owned_total 
CREATE view pieces_available as
select
pieces_total.creators_id,
pieces_total,
IFNull(sum(pieces_owned_total), 0) as pieces_owned_total,
pieces_total - IFNULL(sum(pieces_owned_total), 0) as pieces_available
from pieces_total
left join pieces_owned_total on pieces_owned_total.creators_id = pieces_total.creators_id
group by creators_id
;


CREATE VIEW ask_bid_accept_checker as 
select 
--*,
--sum(bids.pieces)
asks.creators_id,
asks.id as ask_id,
asks.users_id as askers_id,
asks.pieces as ask_pieces,
asks.ask_per_piece,
asks.valid_until as ask_valid_until,
asks.partial_fill as ask_partial_fill,
bids.id as bid_id,
bids.users_id as bidders_id,
bids.pieces as bid_pieces,
bids.bid_per_piece,
bids.valid_until as bid_valid_until,
bids.partial_fill as bid_partial_fill,
bid_per_piece-ask_per_piece as price_per_piece_difference

from 
asks
left join
bids
on asks.creators_id = bids.creators_id
where bid_per_piece >= ask_per_piece
and NOW() < asks.valid_until
and NOW() < bids.valid_until

-- ordering by the highest bidder, and when the asker placed his asc (first in line)
order by asks.id asc,bid_per_piece-ask_per_piece desc
;


-- need 3 views, pieces_owned_accum, pieces_owned_value, rewards_earned, rewards_earned_accum
CREATE VIEW pieces_owned_accum as
select
a.id,
a.owners_id,
a.creators_id,
a.time_,
sum(b.pieces_owned) as pieces_accum
from pieces_owned a, pieces_owned b
WHERE b.owners_id = a.owners_id
and b.time_ <= a.time_
GROUP BY a.id,a.owners_id
ORDER BY a.owners_id, a.creators_id, a.time_
;

CREATE VIEW pieces_owned_value as
select
pieces_owned_accum.owners_id, pieces_owned_accum.creators_id, pieces_owned_accum.time_, price_per_piece * pieces_accum as value
from pieces_owned_accum
inner join prices on pieces_owned_accum.creators_id = prices.creators_id
and pieces_owned_accum.time_ = prices.time_
;




select * from pieces_owned

select *,
TIMESTAMPDIFF(SECOND,prices.time_,rewards.time_) as timediff

from prices
inner join 
rewards
on prices.creators_id = rewards.creators_id 
and prices.time_ >= rewards.time_


/*
select * from pieces_total;
select * from pieces_available;
select * from pieces_owned;
select * from pieces_owned_total;
select * from pieces_owned_accum;
select * from pieces_issued;
select * from users_current_view;
select * from worth;
select * from prices;
select * from ask_bid_accept_checker


select * from pieces_owned order by owners_id, time_ desc
*/

--grant select on bitpieces.* to 'river'@'%' identified by 'asdf';

-- the audit tables and triggers


--ALTER TABLE employees ADD unique INDEX name (first_name, last_name)
