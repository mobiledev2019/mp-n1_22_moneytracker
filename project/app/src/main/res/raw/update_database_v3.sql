-- Thêm budget

CREATE TABLE IF NOT EXISTS tbl_budgets (
    _id integer primary key  AUTOINCREMENT NOT NULL,
    categoryId integer NOT NULL,
    walletId integer NOT NULL,
    amount real NOT null,
    spent real  NOT NULL,
    timeStart integer  NOT NULL,
    timeEnd integer  NOT NULL,
    loop integer NOT NULL,
    status varchar NOT NULL
);