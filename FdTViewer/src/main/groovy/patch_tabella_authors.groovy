import com.forumdeitroll.FdTConfig
import groovy.sql.Sql

Properties properties = new Properties()
FdTConfig.class.getClassLoader().getResource('config.properties').withInputStream {
    properties.load(it)
}

def persistenceName = properties."persistence.name"

def username = properties."persistence.${persistenceName}.username"
def password = properties."persistence.${persistenceName}.password"
def url = properties."persistence.${persistenceName}.url"

Sql sql = Sql.newInstance(url, username, password, "com.mysql.jdbc.Driver")

def totalRows = sql.firstRow('select count(*) from authors').values().find()

def actualRow = 1
sql.eachRow('select nick from authors') { row ->
    def nick = row.getString(1)
    def date = sql.firstRow('select date from messages where author=:nick order by date asc limit 1 ', [nick: nick])
    if (date) {
        sql.executeUpdate('update authors set creationDate=:creationDate where nick=:nick', [creationDate: date.values().find(), nick: nick])
        sql.executeUpdate('update authors set enabled=1 where nick=:nick', [nick: nick])
    } else {
        sql.executeUpdate('update authors set creationDate=null where nick=:nick', [nick: nick])
        sql.executeUpdate('update authors set enabled=0 where nick=:nick', [nick: nick])
    }
    print "\r"
    print "${actualRow}/${totalRows}"
    actualRow++
}
println ""
println "Done"