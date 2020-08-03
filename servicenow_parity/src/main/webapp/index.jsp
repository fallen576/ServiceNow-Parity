<html>
    <head>
        <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
        <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.21/css/jquery.dataTables.css">
        <script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.10.21/js/jquery.dataTables.js"></script>
        <link rel="stylesheet" href="css/main.css"/>
        <script>
            $(document).ready( function () {
                $('#table').DataTable();
            } );
        </script>
    </head>
    <body>
        <div class="container-fluid">
            <div class="row h-100">
                <!--left nav bar-->
                <div class="col-2" style="background-color: #37424F">
                    <div class="mynavbar">
                        <div id="navbar-header">
                            <h4>Select a module</h4>
                        </div>              
                        <nav class="navbar" style="display: block;">
                            <ul class="navbar-nav">
                                <li class="nav-item">
                                    <input name="filter" class="nav-link filter rounded" placeholder="Search for Tables"></input>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link rounded" onclick="">Users</a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link">Tutorials</a>
                                </li>
                                <li class="nav-item">
                                    <a class="nav-link">Articles</a>
                                </li>
                                <li>
                                    <a class="nav-link">Kaitlyn is the greatest</a>
                                </li>
                            </ul>
                        </nav>
                    </div>
                </div>
                <!--main content area-->
                <div class="col-10" style="background-color: #FFFFFF">
                    <table class="table" id="table">
                        <thead>
                            <tr>
                            <th scope="col"></th>
                            <th scope="col">Link to</th>
                            <th scope="col">Last</th>
                            <th scope="col">Handle</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                            <th scope="row">Link to Record</th>
                            <td>Mark</td>
                            <td>Otto</td>
                            <td>@mdo</td>
                            </tr>
                            <tr>
                            <th scope="row">Link to Record</th>
                            <td>Jacob</td>
                            <td>Thornton</td>
                            <td>@fat</td>
                            </tr>
                            <tr>
                            <th scope="row">Link to Record</th>
                            <td>Larry</td>
                            <td>the Bird</td>
                            <td>@twitter</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>  
    </body>
</html>
