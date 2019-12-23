package swordskillsapi;

import org.apache.logging.log4j.Logger;

/**
 * 
 * Wrapper around the basic Logger functions that supports easily disabling all logging for the enclosed Logger.
 *
 */
public class LogWrapper
{
	private boolean enabled = true;

	private final Logger logger;

	public LogWrapper(Logger logger) {
		this.logger = logger;
	}

	/**
	 * Disables logging
	 */
	public void disable() {
		this.enabled = false;
	}

	/**
	 * Enables logging
	 */
	public void enable() {
		this.enabled = true;
	}

	public void debug(String msg) {
		if (this.enabled) {
			this.logger.debug(msg);
		}
	}

	public void error(String msg) {
		if (this.enabled) {
			this.logger.error(msg);
		}
	}

	public void fatal(String msg) {
		if (this.enabled) {
			this.logger.fatal(msg);
		}
	}

	public void info(String msg) {
		if (this.enabled) {
			this.logger.info(msg);
		}
	}

	public void trace(String msg) {
		if (this.enabled) {
			this.logger.trace(msg);
		}
	}

	public void warn(String msg) {
		if (this.enabled) {
			this.logger.warn(msg);
		}
	}
}
