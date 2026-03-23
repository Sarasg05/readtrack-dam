from django.shortcuts import render
from rest_framework import viewsets
from rest_framework.permissions import IsAuthenticated

from .models import AnnualGoal, Author, Genre, Book, Reading, ReadingSession
from .serializers import (
    AnnualGoalSerializer,
    AuthorSerializer,
    GenreSerializer,
    BookSerializer,
    ReadingSerializer,
    ReadingSessionSerializer
)

class BookViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Book.objects.all()
    serializer_class = BookSerializer

class AnnualGoalViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = AnnualGoal.objects.all()
    serializer_class = AnnualGoalSerializer

    def get_queryset(self):
        user = self.request.user
        return AnnualGoal.objects.filter(user=user)

class AuthorViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Author.objects.all()
    serializer_class = AuthorSerializer

class GenreViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Genre.objects.all()
    serializer_class = GenreSerializer

class ReadingViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = Reading.objects.all()
    serializer_class = ReadingSerializer

    def get_queryset(self):
        user = self.request.user
        return Reading.objects.filter(user=self.request.user)

class ReadingSessionViewSet(viewsets.ModelViewSet):
    permission_classes = [IsAuthenticated]
    queryset = ReadingSession.objects.all()
    serializer_class = ReadingSessionSerializer

    def get_queryset(self):
        return ReadingSession.objects.filter(reading__user=self.request.user)



