/*************************************************************************
 *                                                                       *
 * Open Dynamics Engine, Copyright (C) 2001,2002 Russell L. Smith.       *
 * All rights reserved.  Email: russ@q12.org   Web: www.q12.org          *
 * Open Dynamics Engine 4J, Copyright (C) 2009-2014 Tilmann Zaeschke     *
 * All rights reserved.  Email: ode4j@gmx.de   Web: www.ode4j.org        *
 *                                                                       *
 * This library is free software; you can redistribute it and/or         *
 * modify it under the terms of EITHER:                                  *
 *   (1) The GNU Lesser General Public License as published by the Free  *
 *       Software Foundation; either version 2.1 of the License, or (at  *
 *       your option) any later version. The text of the GNU Lesser      *
 *       General Public License is included with this library in the     *
 *       file LICENSE.TXT.                                               *
 *   (2) The BSD-style license that is included with this library in     *
 *       the file ODE-LICENSE-BSD.TXT and ODE4J-LICENSE-BSD.TXT.         *
 *                                                                       *
 * This library is distributed in the hope that it will be useful,       *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the files    *
 * LICENSE.TXT, ODE-LICENSE-BSD.TXT and ODE4J-LICENSE-BSD.TXT for more   *
 * details.                                                              *
 *                                                                       *
 *************************************************************************/
package com.github.antzGames.gdx.ode4j.ode.internal;

import com.badlogic.gdx.Gdx;

/**
 *
 * Error handlers.
 */
public class ErrorHdl {

	    /**
	 * @author Tilmann Zaeschke
	 */
	public static class ErrorJump extends RuntimeException {
		private static final long serialVersionUID = 1L;

	}


	private static ErrorHandler.dMessageFunction error_function = null;
	private static ErrorHandler.dMessageFunction debug_function = null;
	private static ErrorHandler.dMessageFunction message_function = null;


	/**
	 * set a new error, debug or warning handler. if fn is 0, the default handlers
	 * are used.
	 * @param fn message function
	 */
	public static void dSetErrorHandler (ErrorHandler.dMessageFunction fn) {
		error_function = fn;
	}


	/**
	 * set a new error, debug or warning handler. if fn is 0, the default handlers
	 * are used.
	 * @param fn message function
	 */
	public static void dSetDebugHandler (ErrorHandler.dMessageFunction fn) {
		debug_function = fn;
	}


	/**
	 * set a new error, debug or warning handler. if fn is 0, the default handlers
	 * are used.
	 * @param fn message function
	 */
	public static void dSetMessageHandler (ErrorHandler.dMessageFunction fn) {
		message_function = fn;
	}


	/**
	 * @return the current error, debug or warning handler. if the return value is
	 * 0, the default handlers are in place.
	 */
	public static ErrorHandler.dMessageFunction dGetErrorHandler() {
		return error_function;
	}


	/**
	 * @return the current error, debug or warning handler. if the return value is
	 * 0, the default handlers are in place.
	 */
	public static ErrorHandler.dMessageFunction dGetDebugHandler() {
		return debug_function;
	}


	/**
	 * @return the current error, debug or warning handler. if the return value is
	 * 0, the default handlers are in place.
	 */
	public static ErrorHandler.dMessageFunction dGetMessageHandler() {
		return message_function;
	}


	/**
	 * generate a fatal error, debug trap or a message.
	 * @param num error number
	 * @param msg message
	 * @param ap objects
	 */
	public static void dError (int num, String msg, Object ... ap) {
		//  va_list ap;
		//  va_start (ap,msg);
		if (error_function != null) {
			error_function.call (num,msg,ap);
		}
		Gdx.app.log(ap.getClass().getName(), "ODE Error " + num + ": " + msg);
		throw new RuntimeException("#"+num + ": " + msg);
		//System.exit (1);
	}


	/**
	 * generate a fatal error, debug trap or a message.
	 * @param num error number
	 * @param msg message
	 * @param ap objects
	 */
	public static void dDebug (int num, String msg, Object ... ap) {
		//  va_list ap;
		//  va_start (ap,msg);
		if (debug_function != null) {
			debug_function.call (num,msg,ap);
		}
		Gdx.app.debug(ap.getClass().getName(), "ODE INTERNAL ERROR " + " " + num + ": " + msg);
		// *((char *)0) = 0;   ... commit SEGVicide
		//abort();
//		msg += " -> ";
//		for (Object o:ap) msg += o.toString() + " , ";
//		throw new RuntimeException("#"+num + ": " + msg);
		//  System.exit(2);
	}


	/**
	 * generate a fatal error, debug trap or a message.
	 * @param num error number
	 * @param msg message
	 * @param ap objects
	 */
	public static void dMessage (int num, String msg, Object ... ap) {
		//  va_list ap;
		//  va_start (ap,msg);
		if (message_function!=null) {
			message_function.call (num,msg,ap);
		} else {
			//printMessage (num,"ODE Message",msg,ap);
			//Gdx.app.log(ap.getClass().getName(), "ODE Message " + num + ": " + msg);
		}
	}
}
