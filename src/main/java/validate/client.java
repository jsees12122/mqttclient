package validate;

public class client {
    public static void main(String[] args){
        listener listener = new listener();
        while (true){
            listener.produce("/persistence/test");
            System.out.println(listener.consumer());
        }
    }
}
