CREATE TABLE IF NOT EXISTS tbl_wallets(
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    balance REAL NOT NULL,
    currency TEXT NOT NULL,
    walletType INTEGER,
    icon TEXT,
    userId TEXT );

CREATE TABLE IF NOT EXISTS tbl_categories(
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    type INTEGER NOT NULL,
    category TEXT NOT NULL,
    icon TEXT NOT NULL,
    parentId INTEGER,
    CONSTRAINT fk_transaction_parent_id
    FOREIGN KEY (parentId)
    REFERENCES tbl_categories(_id)
    ON UPDATE CASCADE ON DELETE CASCADE);

INSERT INTO tbl_categories VALUES(1,1,'Ăn uống','icon_116.png',null);
INSERT INTO tbl_categories VALUES(2,1,'Cà phê','icon_15.png',1);
INSERT INTO tbl_categories VALUES(3,1,'Ăn chung','icon_2.png',1);
INSERT INTO tbl_categories VALUES(4,1,'Nhà hàng','icon_133.png',1);
INSERT INTO tbl_categories VALUES(5,1,'Hóa đơn & Tiện ích','icon_135.png',null);
INSERT INTO tbl_categories VALUES(6,1,'Điện thoại','icon_134.png',5);
INSERT INTO tbl_categories VALUES(7,1,'Nước','icon_124.png',5);
INSERT INTO tbl_categories VALUES(8,1,'Điện','icon_125.png',5);
INSERT INTO tbl_categories VALUES(9,1,'Gas','icon_139.png',5);
INSERT INTO tbl_categories VALUES(10,1,'Internet','icon_126.png',5);
INSERT INTO tbl_categories VALUES(11,1,'TV','icon_84.png',5);
INSERT INTO tbl_categories VALUES(12,1,'Thuê nhà','icon_136.png',5);
INSERT INTO tbl_categories VALUES(13,1,'Di chuyển','icon_12.png',null);
INSERT INTO tbl_categories VALUES(14,1,'Tắc xi','icon_127.png',13);
INSERT INTO tbl_categories VALUES(15,1,'Gửi xe','icon_128.png',13);
INSERT INTO tbl_categories VALUES(16,1,'Xăng dầu','icon_129.png',13);
INSERT INTO tbl_categories VALUES(17,1,'Bảo dưỡng','icon_130.png',13);
INSERT INTO tbl_categories VALUES(18,1,'Mua sắm','icon_7.png',null);
INSERT INTO tbl_categories VALUES(19,1,'Quần áo','icon_17.png',18);
INSERT INTO tbl_categories VALUES(20,1,'Giày dép','icon_131.png',18);
INSERT INTO tbl_categories VALUES(21,1,'Phụ kiện','icon_63.png',18);
INSERT INTO tbl_categories VALUES(22,1,'Thiết bị điện tử','icon_9.png',18);
INSERT INTO tbl_categories VALUES(23,1,'Bạn bè & Người yêu','icon_1.png',null);
INSERT INTO tbl_categories VALUES(24,1,'Giải trí','icon_49.png',null);
INSERT INTO tbl_categories VALUES(25,1,'Phim ảnh','icon_6.png',24);
INSERT INTO tbl_categories VALUES(26,1,'Trò chơi','icon_33.png',24);
INSERT INTO tbl_categories VALUES(27,1,'Du lịch','icon_122.png',null);
INSERT INTO tbl_categories VALUES(28,1,'Sức khỏe','icon_58.png',null);
INSERT INTO tbl_categories VALUES(29,1,'Thể thao','icon_70.png',28);
INSERT INTO tbl_categories VALUES(30,1,'Khám chữa bệnh','icon_143',28);
INSERT INTO tbl_categories VALUES(31,1,'Thuốc','icon_142.png',28);
INSERT INTO tbl_categories VALUES(32,1,'Chăm sóc cá nhân','icon_132.png',28);
INSERT INTO tbl_categories VALUES(33,1,'Quà tặng & Quyên góp','icon_144.png',null);
INSERT INTO tbl_categories VALUES(34,1,'Cưới hỏi','icon_10.png',33);
INSERT INTO tbl_categories VALUES(35,1,'Tang lễ','icon_11.png',33);
INSERT INTO tbl_categories VALUES(36,1,'Từ thiện','icon_117.png',33);
INSERT INTO tbl_categories VALUES(37,1,'Gia đình','icon_8.png',null);
INSERT INTO tbl_categories VALUES(38,1,'Con cái','icon_38.png',37);
INSERT INTO tbl_categories VALUES(39,1,'Sửa chữa nhà cửa','icon_115.png',37);
INSERT INTO tbl_categories VALUES(40,1,'Dịch vụ gia đinh','icon_54.png',37);
INSERT INTO tbl_categories VALUES(41,1,'Vật nuôi','icon_53.png',37);
INSERT INTO tbl_categories VALUES(42,1,'Giáo dục','icon_113.png',null);
INSERT INTO tbl_categories VALUES(43,1,'Sách','icon_35.png',42);
INSERT INTO tbl_categories VALUES(44,1,'Đầu tư','icon_119.png',null);
INSERT INTO tbl_categories VALUES(45,1,'Kinh doanh','icon_59.png',null);
INSERT INTO tbl_categories VALUES(46,1,'Bảo hiểm','icon_137.png',null);
INSERT INTO tbl_categories VALUES(47,1,'Chi phí','icon_138.png',null);
INSERT INTO tbl_categories VALUES(48,1,'Rút tiền','icon_145.png',null);
INSERT INTO tbl_categories VALUES(49,1,'Khoản chi khác','icon_22.png',null);
INSERT INTO tbl_categories VALUES(50,1,'Mua đồ chung','icon_7.png',null);
INSERT INTO tbl_categories VALUES(51,2,'Thưởng','icon_111.png',null);
INSERT INTO tbl_categories VALUES(52,2,'Tiền lãi','icon_118.png',null);
INSERT INTO tbl_categories VALUES(53,2,'Lương','icon_75.png',null);
INSERT INTO tbl_categories VALUES(54,2,'Được tặng','icon_117.png',null);
INSERT INTO tbl_categories VALUES(55,2,'Bán đồ','icon_121.png',null);
INSERT INTO tbl_categories VALUES(56,2,'Khoản thu khác','icon_23.png',null);
INSERT INTO tbl_categories VALUES(57,3,'Cho vay','icon_120.png',null);
INSERT INTO tbl_categories VALUES(58,3,'Trả nợ','icon_141.png',null);
INSERT INTO tbl_categories VALUES(59,4,'Đi vay','icon_112.png',null);
INSERT INTO tbl_categories VALUES(60,4,'Thu nợ','icon_140.png',null);

CREATE TABLE IF NOT EXISTS tbl_transactions(
    _id INTEGER PRIMARY KEY AUTOINCREMENT,
    currency TEXT NOT NULL,
    trading REAL NOT NULL,
    transaction_date INTEGER NOT NULL,
    note TEXT,
    location TEXT,
    categoryId INTEGER NOT NULL,
    walletId INTEGER NOT NULL,
    CONSTRAINT fk_categories_id
    FOREIGN KEY (categoryId)
    REFERENCES tbl_categories(_id)
    ON UPDATE CASCADE ON DELETE CASCADE ,
    CONSTRAINT fk_transaction_wallet_id
    FOREIGN KEY (walletId)
    REFERENCES tbl_wallets(_id)
    ON UPDATE CASCADE ON DELETE CASCADE);