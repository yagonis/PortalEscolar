import { createBrowserRouter } from "react-router";
import { Root } from "./Root";
import { HomePage } from "./pages/HomePage";
import { AdminPage } from "./pages/AdminPage";
import { NotFoundPage } from "./pages/NotFoundPage";
import { LoginPage } from "./pages/LoginPage";
import { ProtectedRoute } from "./components/ProtectedRoute";
import { UserCreatePage } from "./pages/SignInPage";
import { NewsPage } from "./pages/NewsPage";
import { VotePage } from "./pages/VotePage";

export const router = createBrowserRouter([
  {
    path: "/",
    Component: Root,
    children: [
      { index: true, Component: HomePage },

      {Component: ProtectedRoute,
        children: [
          {path: "admin", Component: AdminPage},
        ],
      },

      { path: "*", Component: NotFoundPage },
      { path: "login", Component: LoginPage },
      { path: "signIn", Component: UserCreatePage,},
      { path: "news", Component: NewsPage},
      { path: "vote", Component: VotePage}
    ],
  },
]);
