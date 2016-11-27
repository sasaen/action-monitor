--DROP TABLE users IF EXISTS;

CREATE TABLE users (
  id         INTEGER PRIMARY KEY,
  name VARCHAR(30),
  email  VARCHAR(50)
);

CREATE TRIGGER ins_after  AFTER  INSERT ON users FOR EACH ROW CALL "com.sasaen.monitor.db.DatabaseTrigger"
CREATE TRIGGER upd_after_row  AFTER  UPDATE ON users FOR EACH ROW CALL "com.sasaen.monitor.db.DatabaseTrigger"
CREATE TRIGGER del_after  AFTER  DELETE ON users FOR EACH ROW CALL "com.sasaen.monitor.db.DatabaseTrigger"

