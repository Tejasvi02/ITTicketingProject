<%@ page isELIgnored="false" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
function toggleAssignRow(rowId) {
    var x = document.getElementById("assignRow-" + rowId);
    if (x.style.display === "none") {
        x.style.display = "table-row";
    } else {
        x.style.display = "none";
    }
}
</script>

<table class="table table-bordered">
    <thead class="thead-dark">
        <tr>
            <th>Email</th>
            <th>Roles</th>
            <th>Action</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach items="${users}" var="user">
            <tr>
                <td>${user.email}</td>
                <td>
                    <c:forEach items="${user.roles}" var="role">
                        <span class="badge badge-info">${role.roleName}</span>
                    </c:forEach>
                </td>
                <td>
                    <c:set var="hasManagerOrAdmin" value="false" />
                    <c:forEach items="${user.roles}" var="role">
                        <c:if test="${role.roleName == 'MANAGER' || role.roleName == 'ADMIN'}">
                            <c:set var="hasManagerOrAdmin" value="true" />
                        </c:if>
                    </c:forEach>
                    <c:if test="${!hasManagerOrAdmin}">
                        <button type="button" class="btn btn-sm btn-primary" onclick="toggleAssignRow(${user.id})">Make Manager</button>
                    </c:if>
                </td>
            </tr>

            <tr id="assignRow-${user.id}" style="display: none;">
                <td colspan="3">
                    <form action="/admin/assign-role" method="post">
                        <input type="hidden" name="userId" value="${user.id}" />
                        <div class="form-group">
                            <label>Assign Users Under Manager:</label>
                            <div class="row">
                                <c:forEach items="${users}" var="u">
                                    <c:set var="uHasAdminRole" value="false" />
                                    <c:forEach items="${u.roles}" var="r">
                                        <c:if test="${r.roleName == 'ADMIN'}">
                                            <c:set var="uHasAdminRole" value="true" />
                                        </c:if>
                                    </c:forEach>

                                    <c:if test="${u.id != user.id && !uHasAdminRole}">
                                        <div class="col-md-3">
                                            <input type="checkbox" name="assignedUserIds" value="${u.id}" />
                                            ${u.email}
                                        </div>
                                    </c:if>
                                </c:forEach>
                            </div>
                        </div>
                        <button type="submit" class="btn btn-sm btn-success">Confirm</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>
