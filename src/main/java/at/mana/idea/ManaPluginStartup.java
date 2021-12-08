package at.mana.idea;

import at.mana.idea.util.HibernateUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class ManaPluginStartup implements StartupActivity
{

    private static final Logger logger = Logger.getInstance( ManaPluginStartup.class );


    @Override
    public void runActivity(@NotNull Project project) {

        /*try(Session session = HibernateUtil.getSessionFactory().openSession()) {



            var tx = session.beginTransaction();
            session.getCriteriaBuilder().createQuery(Measurement.class).getOrderList();
            Descriptor descriptor = new Descriptor();
            descriptor.setHash("12345");
            descriptor.setClassName("className");
            descriptor.setMethodName("methodName");
            var ident = session.save( descriptor );
            Descriptor des = session.get( Descriptor.class, ident );
            System.out.println( des.getClassName() + " " + des.getMethodName() );
            tx.commit();
        }*/

        HibernateUtil.getSessionFactory();

        /*HibernateUtil.executeInTransaction( session -> {
            Descriptor descriptor = new Descriptor();
            descriptor.setHash("12345");
            descriptor.setClassName("className");
            descriptor.setMethodName("methodName");
            session.save( descriptor );
        } );*/


        //ManaProjectService service = ServiceManager.getService(project,  ManaProjectService.class);
        //service.init();
        //MessageBusConnection connection = project.getMessageBus().connect();
        // the project service should be informed whenever files are changed
        //connection.subscribe(VirtualFileManager.VFS_CHANGES, service );
        // initially build model from all mana files


    }

}
