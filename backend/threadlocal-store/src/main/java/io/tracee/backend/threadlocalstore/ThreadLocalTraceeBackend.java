package io.tracee.backend.threadlocalstore;

import io.tracee.BackendBase;

import java.util.HashMap;
import java.util.Map;

class ThreadLocalTraceeBackend extends BackendBase {

	private final ThreadLocalMap<String, String> threadLocalMap;

	public ThreadLocalTraceeBackend() {
		this.threadLocalMap = new ThreadLocalMap<>();
	}


	@Override
	public boolean containsKey(String key) {
		return threadLocalMap.get().containsKey(key);
	}

	@Override
	public int size() {
		return threadLocalMap.get().size();
	}

	@Override
	public boolean isEmpty() {
		return threadLocalMap.get().isEmpty();
	}

	@Override
	public String get(String key) {
		return threadLocalMap.get().get(key);
	}

	@Override
	public void put(String key, String value) {
		threadLocalMap.get().put(key, value);
	}

	@Override
	public void remove(String key) {
		threadLocalMap.get().remove(key);
	}

	@Override
	public void clear() {
		threadLocalMap.get().clear();
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> entries) {
		threadLocalMap.get().putAll(entries);
	}

	@Override
	public Map<String, String> copyToMap() {
		return new HashMap<>(threadLocalMap.get());
	}

	ThreadLocalMap<String, String> getThreadLocalMap() {
		return threadLocalMap;
	}
}
