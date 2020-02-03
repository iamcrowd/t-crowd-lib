package it.unibz.inf.qtl1.terms;

public class Constant extends Function {

	public Constant(String name) {
		super(name,0);
	}
	@Override
	public boolean equals(Object o)
	{
		if (o instanceof Constant)
		{
			Constant c= (Constant) o;
			return this.name.equals(c.name);
		}
		return false;
	}
	@Override
	public int hashCode ()
	{
		return this.getName().hashCode();
	}
}
