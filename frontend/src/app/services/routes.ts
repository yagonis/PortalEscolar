import {LoginPage} from "../pages/LoginPage";
import {HomePage} from "../pages/HomePage";
import {AdminPage} from "../pages/AdminPage";
import {NotFoundPage} from "../pages/NotFoundPage";
import {SignInPage} from "../pages/SignInPage";
import {Root} from "../Root";
import {createBrowserRouter} from "react-router";

export const router = createBrowserRouter([
    {
        path: "/",
        Component: Root,
        Children: [
            {index: true, Component: HomePage},
            {path: "/login", Component: LoginPage},
            {path: "/admin", Component: AdminPage},
            {path: "*", Component: NotFoundPage},
            {path: "/signIn", Component: SignInPage}
        ],
    },
])