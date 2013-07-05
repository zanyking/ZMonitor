package test.eventbus;

import java.lang.reflect.Modifier;

import org.zkoss.monitor.util.eventbus.EventBus;


public class EventBus_CASE1 {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws SecurityException 
	 */
	public static void main(String[] args) throws Exception {

		EventBus<UserEvent> evtBus = 
			new EventBus<UserEvent>(UserEvent.class);
		
		//somewhere else in the system...
		evtBus.listen(new UserListener(){

			public void onUser(UpdateEvent evt) {
				System.out.println(evt+" updated");
			}
			public void onUser(AddEvent evt) {
				System.out.println(evt+" added");
			}
			public void onUser(RemoveEvent evt) {
				System.out.println(evt+" removed");
			}
		});
		
		//another place of the system...
		evtBus.send(new AddEvent(new User("Henri")));
		evtBus.send(new UpdateEvent(new User("Tom")));
		evtBus.send(new RemoveEvent(new User("Ian")));
	
	}
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	public static interface UserListener{
		
		public void onUser(UpdateEvent evt);
		
		public void onUser(AddEvent evt);
		
		public void onUser(RemoveEvent evt);
		
	}
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	public static class AddEvent extends UserEvent{
		public AddEvent(User user) {
			super(user);
		}
	}
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	public static class RemoveEvent extends UserEvent{
		public RemoveEvent(User user) {
			super(user);
		}
	}
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	public static class UpdateEvent extends UserEvent{
		public UpdateEvent(User user) {
			super(user);
		}
	}
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	public static class UserEvent {
		private User user;
		public UserEvent(User user) {
			this.user = user;
		}
		public User getUser() {
			return user;
		}
		public String toString() {
			return  user.toString() ;
		}
	}
	/**
	 * @author Ian YT Tsai(Zanyking)
	 */
	static class User {

		private final String name;

		public User(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "User [name=" + name + "]";
		}
		
	}
}
