/**
 * 
 */
package org.zmonitor.util.concurrent;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Ian YT Tsai(Zanyking)
 *
 */
public class SingleBoundedExecutorService implements ExecutorService{

	private final ArrayBlockingQueue<Runnable> queue;
	private final ThreadPoolExecutor singleExecutor;
	/**
	 * @param capacity
	 * @param threadFactory 
	 */
	public SingleBoundedExecutorService( long aliveTime, ThreadFactory threadFactory){
		queue = new ArrayBlockingQueue<Runnable>(1);
		singleExecutor = new ThreadPoolExecutor(1, 1, aliveTime, TimeUnit.MILLISECONDS, queue, threadFactory);
		singleExecutor.allowCoreThreadTimeOut(true);
	}
	/**
	 * @param capacity
	 * @param threadFactory 
	 */
	public SingleBoundedExecutorService( long aliveTime){
		queue = new ArrayBlockingQueue<Runnable>(1);
		singleExecutor = new ThreadPoolExecutor(1, 1, aliveTime, TimeUnit.MILLISECONDS, queue);
		singleExecutor.allowCoreThreadTimeOut(true);
	}
	protected void finalize(){
		singleExecutor.shutdown();
	}
	public int hashCode() {
		return singleExecutor.hashCode();
	}
	public Future<?> submit(Runnable task) {
		return singleExecutor.submit(task);
	}
	public <T> Future<T> submit(Runnable task, T result) {
		return singleExecutor.submit(task, result);
	}
	public <T> Future<T> submit(Callable<T> task) {
		return singleExecutor.submit(task);
	}
	public boolean equals(Object obj) {
		return singleExecutor.equals(obj);
	}
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
			throws InterruptedException, ExecutionException {
		return singleExecutor.invokeAny(tasks);
	}
	public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
			long timeout, TimeUnit unit) throws InterruptedException,
			ExecutionException, TimeoutException {
		return singleExecutor.invokeAny(tasks, timeout, unit);
	}
	public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
			throws InterruptedException {
		return singleExecutor.invokeAll(tasks);
	}
	public <T> List<Future<T>> invokeAll(
			Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
			throws InterruptedException {
		return singleExecutor.invokeAll(tasks, timeout, unit);
	}
	public String toString() {
		return singleExecutor.toString();
	}
	public void execute(Runnable command) {
		singleExecutor.execute(command);
	}
	public void shutdown() {
		singleExecutor.shutdown();
	}
	public List<Runnable> shutdownNow() {
		return singleExecutor.shutdownNow();
	}
	public boolean isShutdown() {
		return singleExecutor.isShutdown();
	}
	public boolean isTerminating() {
		return singleExecutor.isTerminating();
	}
	public boolean isTerminated() {
		return singleExecutor.isTerminated();
	}
	public boolean awaitTermination(long timeout, TimeUnit unit)
			throws InterruptedException {
		return singleExecutor.awaitTermination(timeout, unit);
	}
	public void setThreadFactory(ThreadFactory threadFactory) {
		singleExecutor.setThreadFactory(threadFactory);
	}
	public ThreadFactory getThreadFactory() {
		return singleExecutor.getThreadFactory();
	}
	public void setRejectedExecutionHandler(RejectedExecutionHandler handler) {
		singleExecutor.setRejectedExecutionHandler(handler);
	}
	public RejectedExecutionHandler getRejectedExecutionHandler() {
		return singleExecutor.getRejectedExecutionHandler();
	}
	public void setCorePoolSize(int corePoolSize) {
		singleExecutor.setCorePoolSize(corePoolSize);
	}
	public int getCorePoolSize() {
		return singleExecutor.getCorePoolSize();
	}
	public boolean prestartCoreThread() {
		return singleExecutor.prestartCoreThread();
	}
	public int prestartAllCoreThreads() {
		return singleExecutor.prestartAllCoreThreads();
	}
	public boolean allowsCoreThreadTimeOut() {
		return singleExecutor.allowsCoreThreadTimeOut();
	}
	public void allowCoreThreadTimeOut(boolean value) {
		singleExecutor.allowCoreThreadTimeOut(value);
	}
	public void setMaximumPoolSize(int maximumPoolSize) {
		singleExecutor.setMaximumPoolSize(maximumPoolSize);
	}
	public int getMaximumPoolSize() {
		return singleExecutor.getMaximumPoolSize();
	}
	public void setKeepAliveTime(long time, TimeUnit unit) {
		singleExecutor.setKeepAliveTime(time, unit);
	}
	public long getKeepAliveTime(TimeUnit unit) {
		return singleExecutor.getKeepAliveTime(unit);
	}
	public BlockingQueue<Runnable> getQueue() {
		return singleExecutor.getQueue();
	}
	public boolean remove(Runnable task) {
		return singleExecutor.remove(task);
	}
	public void purge() {
		singleExecutor.purge();
	}
	public int getPoolSize() {
		return singleExecutor.getPoolSize();
	}
	public int getActiveCount() {
		return singleExecutor.getActiveCount();
	}
	public int getLargestPoolSize() {
		return singleExecutor.getLargestPoolSize();
	}
	public long getTaskCount() {
		return singleExecutor.getTaskCount();
	}
	public long getCompletedTaskCount() {
		return singleExecutor.getCompletedTaskCount();
	}
}
