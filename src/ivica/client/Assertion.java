package ivica.client;

public class Assertion
{

public static boolean NDEBUG = true;

   private static void printStack(String why) {
	      Throwable t = new Throwable(why);
	      t.printStackTrace();
	    //  System.exit(1);
	   }

	   public static void asert(boolean expression, String why) {
	      if (NDEBUG && !expression) {
	         printStack(why);
	      }
	   }
}
