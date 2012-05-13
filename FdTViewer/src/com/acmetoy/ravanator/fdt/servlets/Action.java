package com.acmetoy.ravanator.fdt.servlets;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Action {

	enum Method {
		GET("GET"),
		POST("POST"),
		GETPOST("GETPOST");

		String method = null;
		Method(String method) {
			this.method = method;
		};
		
		@Override
		public String toString() {
			return method;
		}
	}
	
	Method method() default Method.GETPOST;

}
