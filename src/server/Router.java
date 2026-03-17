package server;

import java.lang.reflect.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;


public class Router<T> {
	protected T controller;
	protected HashMap<Integer, Method> routingMap;
	
	public Router(T controller) throws InvalidRouteException {
		this.controller = controller;
		routingMap = buildMap();
	}
	
	// In content, if value == Int, then it is just (int), if it is string, it is: (int-length)(utf-8-string-content)
	public void route(int code, int[] sizes, byte[] content) throws NotFoundException, InvalidContentException {
		Method controllerRoute = routingMap.getOrDefault(code, null);
		if(controllerRoute == null)
			throw new NotFoundException("Expected route definiton (" + code + ").");
		
		Object[] paramValues = new Object[controllerRoute.getParameterCount()]; 
		int index = 0;
		int offset = 0;
		for(Parameter param : controllerRoute.getParameters()) {
			
			if(offset >= content.length)
				throw new InvalidContentException("Content ended early at offset " + offset + ".");
			
			if(param.getType() == int.class) {
				paramValues[index] = decodeInt(content, offset);

			} else if (param.getType() == String.class) {

				
				System.out.println(decodeString(content, offset, sizes[index]));
				paramValues[index] = decodeString(content, offset, sizes[index]);
			}
			
			offset += sizes[index];
			index++;
		}
		
		try {
			controllerRoute.invoke(controller, paramValues);
		} catch (Exception ex) {
			throw new InvalidContentException("Unexpectedly failed to invoke controller route.");
		}
	}
	
	protected HashMap<Integer, Method> buildMap() throws InvalidRouteException {
		HashMap<Integer, Method> map = new HashMap<Integer, Method>();
		for (Method method : controller.getClass().getMethods()) {
			Route[] routeAnnotations = method.getAnnotationsByType(Route.class);
			if(routeAnnotations.length <= 0)
				continue;
			
			for(Route route : routeAnnotations) {
				if(map.containsKey(route.code())) {
					throw new InvalidRouteException("Found duplicate route definition (" + route.code() + ") while attempting to build Controller map.");
				}
				
				Parameter[] parameters = method.getParameters();
				for(Parameter param : parameters) {
					if(int.class != param.getType() && String.class != param.getType()) {
						throw new InvalidRouteException("Route definition parameters may contain only int and String types.");
					}
				}
				
				
				map.put(route.code(), method);
			}
		}
		
		return map;
	}
	
	private int decodeInt(byte[] bytes, int offset) {
		return ByteBuffer.wrap(bytes, offset, Integer.BYTES).getInt();
	}
	
	private String decodeString(byte[] bytes, int offset, int size) {
		return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(bytes, offset, size)).toString();
	}
}
