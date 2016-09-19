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

    public class Pair {
           String word;
           int count;

           public Pair() {};
           
           public Pair(String word, int count) {
               this.word = word;
               this.count = count;
           }
           
           public void setWord(String str) {
                word = str;
           }
           
           public void setCount(int c) {
                count = c;
           }
    }
    
    void printCounts(Dao dao) {
      dao.dbQuery(rs -> new Pair(rs.getString("WORD"), rs.getInt("COUNT")), "SELECT WORD, COUNT FROM WORDS")
          .forEach(p -> System.out.println(p.word + ": " + p.count));
    }
    
Or you could use the ExtractorFactory to create an extractor:

   void printCounts(Dao dao) {
        ExtractorFactory<Pair> factory = new ExtractorFactory<>();
        Extractor<Pair> extractor = factory
                                    .add(Pair::setWord, Extractors.STRING, 1)
                                    .add(Pair::setCount, Extractors.INTEGER, 2)
                                    .factory(Pair::new)
                                    .getExtractor();
         dao.dbQuery(extractor, "SELECT WORD, COUNT FROM WORDS")
                  .forEach(p -> System.out.println(p.word + ": " + p.count));
   }