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
creators_page_fields,
users_deposits,
users_withdrawals,
creators_withdrawals,
badges,
users_badges,
creators_badges,
categories,
creators_categories,
cities,
creators_cities,
currencies, 
timezones
;


DROP VIEW IF EXISTS prices, worth, candlestick_prices, rewards_annualized_pct, pieces_total, pieces_available, pieces_owned_total, users_current_view,
ask_bid_accept_checker, pieces_owned_accum, pieces_owned_value, pieces_owned_value_accum, prices_span, rewards_earned, rewards_earned_accum, pieces_owned_span,
rewards_earned_total, rewards_owed, users_funds, current_users_funds, users_funds_current, users_funds_accum, pieces_owned_value_sum_by_owner, 
pieces_owned_value_sum_by_creator, pieces_owned_value_current_by_owner, pieces_owned_value_current_by_creator, creators_funds, creators_funds_current, rewards_current,
rewards_span, pieces_owned_value_current, prices_for_user,pieces_owned_value_first, users_funds_grouped, users_transactions, rewards_earned_total_by_user, users_activity,
users_reputation, backers_current, backers_current_count, creators_page_fields_view, pieces_issued_view, rewards_owed_to_user, pieces_owned_by_creator,
bids_asks, rewards_view, creators_reputation, creators_transactions, creators_activity, pieces_available_view, bids_asks_current, creators_funds_accum, creators_funds_grouped,
rewards_earned_by_owner_accum, creators_funds_view, creators_search_view, users_settings, creators_settings
;
SET FOREIGN_KEY_CHECKS=1
;
-- The Table creates and indexes

CREATE TABLE currencies
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   iso VARCHAR(56) NOT NULL,
   name VARCHAR(56) NOT NULL,
   unicode VARCHAR(56)  CHARACTER SET utf8 COLLATE utf8_unicode_ci NOT NULL, 
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;
--
--CREATE TABLE timezones
--(
--   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
--   name VARCHAR(56) NOT NULL,
--   offset VARCHAR(56) NOT NULL,
--   created_at TIMESTAMP NOT NULL DEFAULT 0,
--   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
--   UPDATE CURRENT_TIMESTAMP
--)
--;


CREATE TABLE users
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   username VARCHAR(56) UNIQUE NOT NULL,
   password_encrypted TINYTEXT NOT NULL,
   email VARCHAR(56) UNIQUE NOT NULL,
   local_currency_id int(11) NOT NULL DEFAULT 1,
   FOREIGN KEY (local_currency_id) REFERENCES currencies(id),
   precision_ int(11) NOT NULL DEFAULT 6 ,
--   local_timezone_id int(11) DEFAULT 1 NOT NULL,
--   FOREIGN KEY (local_timezone_id) REFERENCES timezones(id),
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
   local_currency_id int(11) NOT NULL DEFAULT 1,
   FOREIGN KEY (local_currency_id) REFERENCES currencies(id),
   precision_ int(11) NOT NULL DEFAULT 6 ,
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


CREATE TABLE categories
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   name VARCHAR(56) UNIQUE NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;

CREATE TABLE creators_categories
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   categories_id int(11) NOT NULL,
   FOREIGN KEY (categories_id) REFERENCES categories(id),
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;

CREATE TABLE cities
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   country VARCHAR(56) NOT NULL,
   city VARCHAR(56) CHARACTER SET utf8 NOT NULL,
   region VARCHAR(56) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;

CREATE TABLE creators_cities
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   creators_id int(11) UNIQUE NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   cities_id int(11) UNIQUE NOT NULL,
   FOREIGN KEY (cities_id) REFERENCES cities(id),
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

CREATE TABLE users_deposits
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   users_id int(11) NOT NULL,
   FOREIGN KEY (users_id) REFERENCES users(id),
   cb_tid VARCHAR(56) NOT NULL,
   time_ DATETIME NOT NULL,
   btc_amount DOUBLE UNSIGNED NOT NULL,
   status VARCHAR(56) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;


CREATE TABLE users_withdrawals
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   users_id int(11) NOT NULL,
   FOREIGN KEY (users_id) REFERENCES users(id),
   cb_tid VARCHAR(56) NOT NULL,
   time_ DATETIME NOT NULL,
   btc_amount DOUBLE UNSIGNED NOT NULL,
   status VARCHAR(56) NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;

CREATE TABLE creators_withdrawals
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   cb_tid VARCHAR(56) NOT NULL,
   time_ DATETIME NOT NULL,
   btc_amount_before_fee DOUBLE UNSIGNED NOT NULL,
   fee DOUBLE UNSIGNED NOT NULL,
   btc_amount_after_fee DOUBLE UNSIGNED NOT NULL,
   status VARCHAR(56) NOT NULL,
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
   price_per_piece DOUBLE UNSIGNED NOT NULL,
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
   from_users_id int(11) NOT NULL,
   FOREIGN KEY (from_users_id) REFERENCES users(id),
   to_users_id int(11) NOT NULL,
   FOREIGN KEY (to_users_id) REFERENCES users(id),
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   time_ DATETIME NOT NULL,
   pieces BIGINT(8) UNSIGNED NOT NULL,
   price_per_piece DOUBLE UNSIGNED NOT NULL,
   total DOUBLE UNSIGNED NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;
CREATE TABLE sales_from_creators
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   from_creators_id int(11) NOT NULL,
   FOREIGN KEY (from_creators_id) REFERENCES creators(id),
   to_users_id int(11) NOT NULL,
   FOREIGN KEY (to_users_id) REFERENCES users(id),
   time_ DATETIME NOT NULL,
   pieces BIGINT(8) UNSIGNED NOT NULL,
   price_per_piece DOUBLE UNSIGNED NOT NULL,
   total DOUBLE UNSIGNED NOT NULL,
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
   reward_per_piece_per_year DOUBLE UNSIGNED NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;

CREATE TABLE badges
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   name VARCHAR(56) NOT NULL,
   points int(11) NOT NULL DEFAULT 10,
   description TINYTEXT NOT NULL,
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;




CREATE TABLE users_badges
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   users_id int(11) NOT NULL,
   FOREIGN KEY (users_id) REFERENCES users(id),
   badges_id int(11) NOT NULL,
   FOREIGN KEY (badges_id) REFERENCES badges(id),
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;

CREATE TABLE creators_badges
(
   id int(11) DEFAULT NULL auto_increment PRIMARY KEY,
   creators_id int(11) NOT NULL,
   FOREIGN KEY (creators_id) REFERENCES creators(id),
   badges_id int(11) NOT NULL,
   FOREIGN KEY (badges_id) REFERENCES badges(id),
   created_at TIMESTAMP NOT NULL DEFAULT 0,
   updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON
   UPDATE CURRENT_TIMESTAMP
)
;




-- Views

CREATE VIEW prices AS
SELECT
creators_id, creators.username as creators_name, time_, price_per_piece
FROM sales_from_users
inner join creators
on sales_from_users.creators_id = creators.id
union
SELECT
from_creators_id as creators_id, creators.username as creators_name, time_, price_per_piece as price_per_piece
FROM sales_from_creators
inner join creators
on sales_from_creators.from_creators_id = creators.id

order by creators_id, time_
;


CREATE VIEW prices_span as
select a.creators_id, a.time_, 
IFNULL(b.time_, NOW()) as end_time_, 
TIMESTAMPDIFF(SECOND,a.time_,IFNULL(b.time_, NOW())) as timediff_seconds,
a.price_per_piece
from prices a
left join 
prices b
on a.creators_id = b.creators_id
and a.time_ < b.time_
group by creators_id, a.time_, a.price_per_piece;

CREATE VIEW pieces_owned_span as
select a.owners_id, a.creators_id, a.time_ as start_time_, 
IFNULL(b.time_, NOW()) as end_time_, 
TIMESTAMPDIFF(SECOND,a.time_,IFNULL(b.time_, NOW())) as timediff_seconds,
a.pieces_owned
from pieces_owned a
left join 
pieces_owned b
on a.creators_id = b.creators_id
and a.owners_id = b.owners_id
and a.time_ < b.time_
group by a.creators_id, a.owners_id, a.time_, a.pieces_owned
order by a.owners_id, a.creators_id, a.time_;



CREATE VIEW rewards_span as
select a.creators_id, a.time_, 
IFNULL(b.time_, NOW()) as end_time_, 
TIMESTAMPDIFF(SECOND,a.time_,IFNULL(b.time_, NOW())) as timediff_seconds,
a.reward_per_piece_per_year
from rewards a
left join 
rewards b
on a.creators_id = b.creators_id
and a.time_ < b.time_
group by creators_id, a.time_, a.reward_per_piece_per_year;



CREATE VIEW pieces_total as
select 
creators_id, sum(pieces_issued) as pieces_total
from pieces_issued
where time_ <= NOW()
group by creators_id;

CREATE VIEW pieces_owned_total as
select 
pieces_owned.owners_id, pieces_owned.creators_id, sum(pieces_owned.pieces_owned) as pieces_owned_total
from pieces_owned
group by owners_id, creators_id
order by owners_id, creators_id;





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
a.owners_id,
a.creators_id,
users.username as owners_name, 
creators.username as creators_username,
a.start_time_,
a.end_time_, 
TIMESTAMPDIFF(SECOND,a.start_time_,IFNULL(a.end_time_, NOW())) as timediff_seconds,
a.pieces_owned,
--b.pieces_owned,
sum(b.pieces_owned) as pieces_accum
from pieces_owned_span a, pieces_owned_span b, creators, users
WHERE b.owners_id = a.owners_id
and b.creators_id = a.creators_id
and b.start_time_ <= a.start_time_
and a.creators_id = creators.id
and a.owners_id = users.id
GROUP BY a.creators_id, a.owners_id, a.start_time_, a.pieces_owned
ORDER BY a.owners_id, a.creators_id, a.start_time_
;

CREATE VIEW pieces_owned_value as 
select
pieces_owned_span.owners_id,
pieces_owned_span.creators_id,
pieces_owned_span.start_time_,
pieces_owned_span.end_time_,
prices_span.time_ as price_time_,
price_per_piece,
pieces_owned,
price_per_piece * pieces_owned as value
from pieces_owned_span
inner join prices_span on pieces_owned_span.creators_id = prices_span.creators_id
and (prices_span.time_ >= pieces_owned_span.start_time_ and prices_span.end_time_ <= pieces_owned_span.end_time_)
order by pieces_owned_span.owners_id,
pieces_owned_span.creators_id,prices_span.time_;

CREATE VIEW pieces_owned_value_current as 
select pieces_owned_total.owners_id, 
users.username as owners_name,
pieces_owned_total.creators_id,
creators.username as creators_username,
sum(pieces_owned_total) as pieces_total,
sum(pieces_owned_total * price_per_piece) as value_total_current
from pieces_owned_total
inner join prices_span
on pieces_owned_total.creators_id = prices_span.creators_id
and prices_span.end_time_ = NOW()
inner join creators
on pieces_owned_total.creators_id = creators.id
inner join users
on pieces_owned_total.owners_id = users.id
where pieces_owned_total >0
group by pieces_owned_total.owners_id, pieces_owned_total.creators_id;





CREATE VIEW pieces_owned_value_current_by_creator as 
select creators_id,
username, 
sum(value_total_current) as value_total_current
from pieces_owned_value_current
inner join creators
on pieces_owned_value_current.creators_id = creators.id
group by creators_id;




CREATE VIEW pieces_owned_by_creator as
select creators_id, start_time_, end_time_, sum(pieces_owned) as pieces_owned_sum
from pieces_owned_span
group by creators_id
having sum(pieces_owned) > 0;
-- the pieces owned query





CREATE VIEW bids_asks as
select creators.username as creators_name,
time_, 
valid_until, 
users.username as users_name, 
bids.users_id, 
'bid' as type,
pieces,
bid_per_piece as price_per_piece,
bid_per_piece * pieces as total
from bids
inner join creators
on bids.creators_id = creators.id
inner join users
on bids.users_id = users.id
--where bids.valid_until >= NOW()
union
select creators.username as creators_name,
time_, 
valid_until, 
users.username as users_name, 
asks.users_id,
'ask' as type,
pieces,
ask_per_piece as price_per_piece,
ask_per_piece * pieces as total
from asks
inner join creators
on asks.creators_id = creators.id
inner join users
on asks.users_id = users.id
--where asks.valid_until >= NOW()
order by creators_name, time_ desc;



CREATE VIEW bids_asks_current as 
select creators_name, time_, users_name, users_id, type, pieces, price_per_piece, total as total_current
from bids_asks
where valid_until >= NOW()
order by creators_name, time_ desc;






CREATE VIEW rewards_view as 
select username as creators_name, time_, reward_per_piece_per_year
from rewards
inner join creators
on rewards.creators_id = creators.id
order by username, time_;









CREATE VIEW pieces_owned_value_current_by_owner as 
select owners_id, owners_name, 
sum(value_total_current) as value_total_current
from pieces_owned_value_current
group by owners_id;






CREATE VIEW pieces_owned_value_accum as
select
pieces_owned_accum.owners_id,
pieces_owned_accum.creators_id,
users.username as owners_name,
creators.username as creators_username,
pieces_owned_accum.start_time_,
pieces_owned_accum.end_time_,
prices_span.time_ as price_time_,
prices_span.end_time_ as price_end_time_,
prices_span.timediff_seconds,
price_per_piece,
pieces_accum,
price_per_piece * pieces_accum as value_accum
from pieces_owned_accum
inner join prices_span on pieces_owned_accum.creators_id = prices_span.creators_id
and (prices_span.time_ >= pieces_owned_accum.start_time_ and prices_span.end_time_ <= pieces_owned_accum.end_time_)
inner join creators
on pieces_owned_accum.creators_id = creators.id
inner join users
on pieces_owned_accum.owners_id = users.id
order by pieces_owned_accum.owners_id,
pieces_owned_accum.creators_id,
prices_span.time_
--and (prices_span.time_ >= pieces_owned_accum.time_ and prices_span.end_time_ >= pieces_owned_accum.time_)
--and prices_span.end_time_ >= pieces_owned_accum.time_
--group by pieces_owned_accum.owners_id,pieces_owned_accum.creators_id, pieces_accum
--group by pieces_owned_accum.owners_id,pieces_owned_accum.creators_id, pieces_owned_accum.time_
;

CREATE VIEW worth AS
select creators_id, creators_username, price_time_, sum(value_accum) as worth
from pieces_owned_value_accum
group by creators_id, creators_username, price_time_
order by creators_id, price_time_;



CREATE VIEW rewards_earned as 
select pieces_owned_accum.owners_id,
pieces_owned_accum.owners_name,
pieces_owned_accum.creators_id,
pieces_owned_accum.creators_username,
pieces_owned_accum.start_time_,
pieces_owned_accum.end_time_,
pieces_owned_accum.timediff_seconds,
pieces_owned_accum.pieces_accum,
rewards_span.time_ as rewards_start_time_, 
rewards_span.end_time_ as rewards_end_time_,
rewards_span.reward_per_piece_per_year,
pieces_accum * pieces_owned_accum.timediff_seconds*reward_per_piece_per_year/3.15569E7 as reward_earned
from pieces_owned_accum
inner join rewards_span
on pieces_owned_accum.creators_id = rewards_span.creators_id
and (pieces_owned_accum.start_time_ >= rewards_span.time_ and pieces_owned_accum.end_time_ <= rewards_span.end_time_)
and pieces_accum > 0 
order by owners_id, pieces_owned_accum.start_time_;



/*
This is the old way of calculating rewards, which is very incorrect

CREATE VIEW rewards_earned as
select pieces_owned_value_accum.owners_id, 
pieces_owned_value_accum.creators_id, 
pieces_owned_value_accum.owners_name,
creators.username as creators_username,
price_time_, 
pieces_owned_value_accum.end_time_, pieces_owned_value_accum.timediff_seconds, value_accum, 
rewards_span.time_ as rewards_start_time_, 
rewards_span.end_time_ as rewards_end_time_,
reward_pct, 
value_accum*(EXP(reward_pct*pieces_owned_value_accum.timediff_seconds/3.15569E7)-1) as reward_earned
from pieces_owned_value_accum
inner join rewards_span on pieces_owned_value_accum.creators_id = rewards_span.creators_id
and (pieces_owned_value_accum.price_time_ >= rewards_span.time_ and pieces_owned_value_accum.end_time_ <= rewards_span.end_time_)
inner join creators
on pieces_owned_value_accum.creators_id = creators.id
;
*/





CREATE VIEW rewards_earned_total as
select owners_id, creators_id, owners_name, creators_username, sum(reward_earned) as reward_earned_total
from rewards_earned
group by owners_id, creators_id;





CREATE VIEW rewards_earned_total_by_user as
select owners_id, owners_name, sum(reward_earned) as reward_earned_total
from rewards_earned
group by owners_id;

CREATE VIEW rewards_owed_to_user as
select creators_id, creators_username, owners_id, users.username as owners_name, sum(reward_earned) as total_owed
from rewards_earned
inner join users
on owners_id = users.id
group by creators_id, owners_id, creators_username
order by creators_id, sum(reward_earned) desc;




CREATE VIEW rewards_owed as
select creators_id, creators_username, sum(reward_earned) as total_owed
from rewards_earned
group by creators_id, creators_username;



CREATE VIEW pieces_issued_view as 
select creators.username as creators_name, time_, pieces_issued, price_per_piece 
from pieces_issued
inner join creators
on pieces_issued.creators_id = creators.id
order by creators.username, time_;






-- This is deposits - withdrawals + rewards earned - purchases from creator - purchases from user + buys from user
CREATE VIEW users_funds as 
select to_users_id as users_id, users.username as owners_name, time_, -1*total as funds from 
sales_from_creators
inner join users
on sales_from_creators.to_users_id = users.id
union 
select from_users_id, users.username as owners_name, time_, total from 
sales_from_users
inner join users
on sales_from_users.from_users_id = users.id
union
select to_users_id, users.username as owners_name, time_, -1*total from 
sales_from_users
inner join users
on sales_from_users.to_users_id = users.id
union
-- use end time, because it shows the last time of rewards
select owners_id, owners_name, end_time_, reward_earned as funds from 
rewards_earned
union
select users_id, users.username as owners_name, time_, btc_amount as funds from 
users_deposits
inner join users
on users_deposits.users_id = users.id
where status='completed'
union
select users_id, users.username as owners_name, time_, -1*btc_amount as funds from 
users_withdrawals
inner join users
on users_withdrawals.users_id = users.id
where status='completed'
order by users_id, time_, funds;




CREATE VIEW users_funds_grouped as
select 
users_id, owners_name, time_, sum(funds) as funds
from users_funds
group by users_id, time_;

CREATE VIEW users_funds_accum as 
select a.users_id, a.owners_name, a.time_, 
--a.funds, 
sum(b.funds) as funds_accum
from users_funds_grouped a, users_funds_grouped b
WHERE b.users_id = a.users_id
and b.time_ <= a.time_
GROUP BY a.users_id, a.time_, a.funds;



CREATE VIEW users_funds_current as
select users_id, owners_name, sum(funds) as current_funds from 
users_funds
group by users_id;

CREATE VIEW creators_funds as 
select creators_id, end_time_ as time_, -1*sum(reward_earned) as funds from 
rewards_earned
group by creators_id, end_time_
union
select from_creators_id as creators_id, time_, total as funds from 
sales_from_creators
union
select creators_id, time_, -1*btc_amount_before_fee
from creators_withdrawals
order by creators_id, time_, funds;

CREATE VIEW creators_funds_view as
select creators_id, creators.username as creator_name, time_, funds from creators_funds
inner join 
creators on
creators_funds.creators_id = creators.id
order by creators_id, time_;

CREATE VIEW creators_funds_grouped as
select 
creators_id, time_, sum(funds) as funds
from creators_funds
group by creators_id, time_;

CREATE VIEW creators_funds_accum as 
select a.creators_id, creators.username as creators_name, a.time_, 
--a.funds, 
sum(b.funds) as funds_accum
from creators_funds_grouped a, creators_funds_grouped b, creators
WHERE b.creators_id = a.creators_id
and b.time_ <= a.time_
and a.creators_id = creators.id
GROUP BY a.creators_id, a.time_, a.funds;



CREATE VIEW creators_funds_current as
select creators_id, creators.username as creators_name, sum(funds) as current_funds from 
creators_funds
inner join creators
on creators_id = creators.id
group by creators_id;



CREATE VIEW rewards_current as 
select * from rewards_span
where end_time_ = NOW();

CREATE VIEW pieces_owned_value_first as
select owners_id, creators_id, min(price_time_) as price_time_, value_accum
from pieces_owned_value_accum
group by owners_id, creators_id;

CREATE VIEW prices_for_user as 
select pieces_owned_value_current.owners_id, 
pieces_owned_value_current.creators_id,
pieces_owned_value_current.owners_name,
creators.username as creators_username,
prices.time_,
prices.price_per_piece
from pieces_owned_value_current
inner join
prices
on pieces_owned_value_current.creators_id = prices.creators_id
inner join creators
on pieces_owned_value_current.creators_id = creators.id
where value_total_current > 0
order by owners_id, creators_id, time_;




CREATE VIEW users_transactions as 
select to_users_id as users_id, 
users.username as owners_name, 
time_, 
'buy' as type,
creators.username as recipient,
pieces, 
-1*total as total
from 
sales_from_creators
inner join creators
on sales_from_creators.from_creators_id = creators.id
inner join users
on sales_from_creators.to_users_id = users.id
union
 
select from_users_id, 
b.username as owners_name,
time_, 
'sell' as type,
a.username as recipient, 
pieces,
total 
from 
sales_from_users
inner join
users a
on sales_from_users.to_users_id = a.id
inner join 
users b
on sales_from_users.from_users_id = b.id
union

select to_users_id, 
b.username as owners_name,
time_, 
'buy' as type,
a.username as recipient, 
pieces,
-1*total from 
sales_from_users
inner join
users a
on sales_from_users.from_users_id = a.id
inner join 
users b
on sales_from_users.to_users_id = b.id
union

select users_id, 
users.username as owners_name,
time_, 
concat('deposit(', status, ')') as type,
'' as recipient, 
'' as pieces, 
btc_amount as funds 
from 
users_deposits
inner join users
on users_deposits.users_id = users.id

union
select users_id, 
users.username as owners_name,
time_,
concat('withdrawal(', status, ')') as type, 
'' as recipient, 
'' as pieces,
-1*btc_amount as funds from 
users_withdrawals
inner join users
on users_withdrawals.users_id = users.id
order by users_id, time_ desc;




CREATE VIEW users_activity as 
select users_id, 
owners_name, 
time_, 
type,
recipient,
pieces,
total
from users_transactions
union 
select users_id, 
users.username as owners_name,
time_, 
'bid' as type,
creators.username as recipient,
pieces,
bid_per_piece*pieces as funds
from bids
inner join creators
on bids.creators_id = creators.id
inner join users
on bids.users_id = users.id
union
select users_id, 
users.username as owners_name, 
time_, 
'ask' as type,
creators.username as recipient,
pieces,
ask_per_piece*pieces as funds
from asks
inner join creators
on asks.creators_id = creators.id
inner join users
on asks.users_id = users.id
order by users_id, time_ desc;



CREATE VIEW creators_transactions as 
select creators.username as creators_name,
time_,
concat('withdrawal(', status, ')') as type, 
'' as recipient, 
'' as pieces, 
-1*btc_amount_before_fee as funds from 
creators_withdrawals
inner join creators
on creators_withdrawals.creators_id = creators.id
union
select creators.username as creators_name, 
time_, 
'buy' as type,
users.username as recipient,
pieces,
total as funds
from 
sales_from_creators
inner join creators
on sales_from_creators.from_creators_id = creators.id
inner join users
on to_users_id = users.id
order by creators_name, time_ desc;



-- Their transactions(sales, withdrawals), Their bids and asks, sales amongst their users, and their pieces issued
CREATE VIEW creators_activity as
select creators_name, time_, type, recipient, pieces, funds
from creators_transactions
union 
select creators_name, time_, type, users_name, pieces, total from bids_asks
union
select creators_name, time_,
'issue pieces' as type,
'' as recipient, 
pieces_issued,
pieces_issued*price_per_piece as funds
from pieces_issued_view
union
select creators.username as creators_name,
time_, 
'sale between users' as type,
users.username as recipient,
pieces,
price_per_piece*pieces as funds
from sales_from_users
inner join creators
on sales_from_users.creators_id = creators.id
inner join users
on sales_from_users.to_users_id = users.id
order by creators_name, time_ desc;











CREATE VIEW users_reputation as
select users_id, 
users.username as owners_name,
sum(points) as reputation
from users_badges
inner join badges
on users_badges.badges_id = badges.id
inner join users
on users_badges.users_id = users.id
group by users_id;

CREATE VIEW creators_reputation as
select creators_id, creators.username as creators_name,
sum(points) as reputation
from creators_badges
inner join badges
on creators_badges.badges_id = badges.id
inner join creators
on creators_badges.creators_id = creators.id
group by creators_id;


CREATE VIEW backers_current as
select owners_id, users.username as users_username, creators_id, creators_username, pieces_owned_value_current.pieces_total, value_total_current
from pieces_owned_value_current
inner join users 
on pieces_owned_value_current.owners_id = users.id
order by creators_id, value_total_current desc;





CREATE VIEW backers_current_count as 
select creators_username, creators_id, count(*) as number_of_backers 
from backers_current
group by creators_id, creators_username;



CREATE VIEW creators_page_fields_view as
select creators_id, username, main_body from creators_page_fields
inner join 
creators
on creators_page_fields.creators_id = creators.id;

CREATE VIEW pieces_available_view as
select creators.username as creators_name, 
pieces_total, 
pieces_owned_total,
pieces_available 
from pieces_available
inner join creators
on pieces_available.creators_id = creators.id;


-- TODO not sure what the time column on this should be
CREATE VIEW rewards_earned_accum as
select a.owners_id, a.owners_name, a.creators_username, a.start_time_, a.reward_earned,
sum(b.reward_earned) as reward_accum
from rewards_earned a, rewards_earned b
where b.owners_id = a.owners_id 
and b.creators_username = a.creators_username
and b.start_time_ <= a.start_time_
GROUP BY a.owners_id, a.creators_username, a.start_time_, a.reward_earned
ORDER BY a.owners_id, a.creators_username, a.start_time_;

CREATE VIEW prices_current as
select prices.creators_id, prices.creators_name, price_per_piece 
from prices
inner join max_price_times
on time_ = max_price_times.max_time_;


CREATE VIEW max_price_times as
SELECT   creators_id, creators_name, max(time_) as max_time_
FROM     prices
GROUP BY creators_id;

CREATE VIEW creators_search_view as 
select creators.id as creators_id,
creators.username as creators_name, 
GROUP_CONCAT(categories.name) as category_names, 
pieces_owned_value_current_by_creator.value_total_current as worth_current,
prices_current.price_per_piece as price_current,
rewards_current.reward_per_piece_per_year,
CONCAT(format(rewards_current.reward_per_piece_per_year/prices_current.price_per_piece*100,2),'%') as reward_yield_current,
backers_current_count.number_of_backers
from creators
inner join pieces_owned_value_current_by_creator
on pieces_owned_value_current_by_creator.creators_id = creators.id
inner join rewards_current
on rewards_current.creators_id = creators.id
inner join backers_current_count
on backers_current_count.creators_id = creators.id
inner join creators_categories
on creators_categories.creators_id = creators.id
inner join categories on
creators_categories.categories_id = categories.id
inner join prices_current
on creators.username = prices_current.creators_name
group by creators.id;







CREATE VIEW users_settings as 
select users.id, 
username, 
email, 
precision_,
currencies.iso as curr_iso,
currencies.name as curr_name 
from users
inner join 
currencies
on users.local_currency_id = currencies.id;

CREATE VIEW creators_settings as 
select creators.id, 
username, 
email, 
precision_,
currencies.iso as curr_iso,
currencies.name as curr_name 
from creators
inner join 
currencies
on creators.local_currency_id = currencies.id;







/*
CREATE VIEW date_testing as
select id, concat('/Date(',UNIX_TIMESTAMP(time_),')/') from bids




select UNIX_TIMESTAMP(time_) from bids
where UNIX_TIMESTAMP(time_) = FROM_UNIXTIME(1405716372)

CREATE VIEW annualized_return as
select pieces_owned_value_first.owners_id, pieces_owned_value_first.creators_id,
value_accum as beg_val,
price_time_ as start_time_, 
value_total as current_value,
reward_earned_total as current_reward,
reward_earned_total + value_total as end_val,
TIMESTAMPDIFF(SECOND,price_time_,NOW())/3.15569E7 as timediff_in_years,
(11.6051628446/value_accum) as derp,
POW(11.6051628446/value_accum as decimal(11,5)),1.0/.0003255706)-1 as ret
from pieces_owned_value_first
inner join pieces_owned_value_current
on pieces_owned_value_first.creators_id = pieces_owned_value_current.creators_id
and pieces_owned_value_first.owners_id = pieces_owned_value_current.owners_id
inner join rewards_earned_total
on pieces_owned_value_first.creators_id = rewards_earned_total.creators_id
and pieces_owned_value_first.owners_id = rewards_earned_total.owners_id

*/




/*

select * from pieces_total;
select * from pieces_available;
select * from pieces_owned order by owners_id, creators_id, time_;
select * from pieces_owned_span
select * from prices_span
select * from pieces_owned_total;
select * from pieces_owned_accum;
select * from pieces_owned_value_accum;
select * from pieces_issued;
select * from users_current_view;
select * from worth;
select * from prices;
select * from ask_bid_accept_checker
select * from rewards_earned
select * from users_transactions
select * from creators_transactions order by creators_name, time_ 

select * from pieces_owned order by owners_id, time_ desc
*/

--grant select on bitpieces.* to 'river'@'%' identified by 'asdf';

-- the audit tables and triggers


--ALTER TABLE employees ADD unique INDEX name (first_name, last_name)
