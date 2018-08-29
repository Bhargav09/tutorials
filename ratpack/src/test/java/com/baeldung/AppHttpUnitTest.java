package com.baeldung;

import com.baeldung.model.Employee;
import org.junit.Test;
import ratpack.exec.Promise;
import ratpack.func.Action;
import ratpack.handling.Chain;
import ratpack.registry.Registry;
import ratpack.test.embed.EmbeddedApp;
import ratpack.test.exec.ExecHarness;

import static org.junit.Assert.assertEquals;

public class AppHttpUnitTest {

    @Test
    public void givenAnyUri_GetEmployeeFromSameRegistry() throws Exception {
        final Action<Chain> chainAction = chain -> chain.prefix("employee/:id", empChain -> empChain.all(ctx -> {
            Long id = Long.valueOf(ctx.getPathTokens()
                    .get("id"));
            Employee employee = new Employee(id, "Mr", "NY");
            ctx.next(Registry.single(Employee.class, employee));
        })
                .get("name", ctx -> {
                    Employee employee = ctx.get(Employee.class);
                    ctx.getResponse()
                            .send("Name of employee with ID " + employee.getId() + " is " + employee.getName());
                })
                .get("title", ctx -> {
                    Employee employee = ctx.get(Employee.class);
                    ctx.getResponse()
                            .send("Title of employee with ID " + employee.getId() + " is " + employee.getTitle());
                }));
        EmbeddedApp.fromHandlers(chainAction)
                .test(testHttpClient -> {
                    assertEquals("Name of employee with ID 1 is NY", testHttpClient.get("employee/1/name")
                            .getBody()
                            .getText());
                    assertEquals("Title of employee with ID 1 is Mr", testHttpClient.get("employee/1/title")
                            .getBody()
                            .getText());
                });
    }

    @Test
    public void givenSyncDataSource_GetDataFromPromise() throws Exception {
        String value = ExecHarness.yieldSingle(execution -> Promise.sync(() -> "Foo"))
                .getValueOrThrow();
        assertEquals("Foo", value);
    }
}
