import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.Context;
import javax.faces.validator.BeanValidator;
import javax.inject.*;

@Named
@SessionScoped
public class TestPersistence implements java.io.Serializable {
	
	private static final long serialVersionUID = 1L;
	
		
	public String test() {
		
		return "Uitdaging 0.0.1";
		
	}
	
	

	private static long getSerialversionuid() {return serialVersionUID;}
	
	
	
}
