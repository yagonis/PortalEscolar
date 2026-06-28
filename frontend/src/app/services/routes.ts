import {LoginPage} from "./pages/LoginPage";

export const router = createBrowserRouter([
    {
        path: "/",
        Component: Root,
        Children: [
            {index: true, Component: HomePage},
            {path: "/login", Component: LoginPage},
            {path: "/admin", Component: AdminPage},
            {path: "*", Component: NotFoundPage},
        ],
    },
]),