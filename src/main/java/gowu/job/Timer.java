package gowu.job;

public class Timer
{
	private final String label;
	private long t0;

	public Timer() {
		this(null);
	}

	public Timer(String label) {
		this.label = label;
		reset();
	}

	public void reset() {
		this.t0 = System.currentTimeMillis();
	}

	public String formatElapsedTime() {
		return String.format(
				"%.1f second(s)",
				(float)(System.currentTimeMillis() - this.t0) / 1000);
	}

	public String toString() {
		return String.format(
				"%s: took %s",
				this.label,
				formatElapsedTime());
	}

	public long getStartTime() {
		return t0;
	}

	public long getElapsedTime() {
		return System.currentTimeMillis() - this.t0;
	}
}
