# Example
The following is a contrived example just to give you an idea of this codes basic use.

## A Word List
You're storing the words from a document in an in memory database for word count analysis. The table has the following schema:

    CREATE TABLE WORDS ( 
      WORD CHAR(80) NOT NULL,
      COUNT INT,
      UNIQUE(WORD)
    );

You're writing a Java data access object (DAO) to allow you to do some basic work with the schema using fun-jdbc.  

    public class Dao implements DbAccessor {
        private final static String DRIVER = "org.h2.Driver";
        private final static String URL = "jdbc:h2:mem:";

        public InMemWordsDatabase() throws ClassNotFoundException {
            Class.forName(DRIVER);
        }

        @Override
        public Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL);
        }
    }

The above gets you most of the basics in one go. With that Dao you can do things like:

    void printAllWords(Dao dao) {
       dao.dbQuery(rs -> rs.getString("WORD"), "SELECT WORD FROM WORDS")
           .forEach(w -> System.out.println(w));
    }
    
Or to see if a given word appears:

    void boolean hasWord(Dao dao, String word) {
      return dao.dbFind(rs -> rs.getString("WORD"), 
            "SELECT * FROM WORDS WHERE WORD = '%s'", word)
            .isPresent();
    }
    
And of course your Extractor can be more complex:

    private class Pair {
           final String word;
           final int count;

           private Pair(String word, int count) {
               this.word = word;
               this.count = count;
           }
    }
    
    void printCounts(Dao dao) {
      dao.dbQuery(rs -> new Pair(rs.getString("WORD"), rs.getInt("COUNT")), "SELECT * FROM WORDS")
          .forEach(p -> System.out.println(p.word + ": " + p.count));
    }
    
To read more about the thinking behind this code read my [blog post](http://nwillc.wordpress.com/2014/09/27/a-little-java-8-goodness-in-jdbc) on the topic.