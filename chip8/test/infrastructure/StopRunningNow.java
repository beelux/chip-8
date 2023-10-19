package infrastructure;
/* We are using Java here purely such that we can use the
   @SuppressWarnings("deprecation") annotation to
   make the compiler stop complaining about this unsafe (but only!) way
   of killing the test thread on an infinite loop.
 */
public class StopRunningNow {
    @SuppressWarnings("deprecation")
    public static void stopRunningNowUnsafe(Thread thread) {
        thread.stop();  // deprecated. unsafe. do not use
    }
}
